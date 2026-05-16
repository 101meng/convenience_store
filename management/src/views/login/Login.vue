<template>
  <div class="min-h-screen bg-slate-50 flex items-center justify-center p-4">
    <!-- 降低了阴影厚重感，调整了圆角 -->
    <div class="bg-white w-full max-w-[400px] rounded-layout p-10 shadow-[0_10px_40px_-10px_rgba(0,0,0,0.04)] border border-slate-100/50 text-center">
      <div class="w-14 h-14 bg-primary rounded-xl mx-auto flex items-center justify-center shadow-md shadow-indigo-100 mb-6">
        <el-icon color="white" :size="28"><Box /></el-icon>
      </div>
      
      <h2 class="text-2xl font-bold text-slate-800 tracking-tight">Convenience Store</h2>
      <p class="text-xs font-medium text-slate-400 mt-1 mb-8">Retail Admin Portal</p>

      <div class="space-y-5 text-left">
        <div>
          <label class="text-[11px] font-semibold text-slate-500 ml-1">Phone Number</label>
          <el-input v-model="form.phone" placeholder="Enter phone number" class="bento-input mt-1.5" />
        </div>
        
        <div>
          <label class="text-[11px] font-semibold text-slate-500 ml-1">Verification Code</label>
          <div class="flex gap-3 mt-1.5">
            <el-input v-model="form.code" placeholder="6-digit code" class="bento-input flex-1" />
            <el-button @click="handleSendCode" :disabled="countdown > 0" class="h-11 rounded-xl font-semibold border-slate-200 text-slate-600 px-5 hover:bg-slate-50">
              {{ countdown > 0 ? `${countdown}s` : 'Send' }}
            </el-button>
          </div>
        </div>

        <el-button type="primary" color="#4f46e5" @click="handleLogin" :loading="loading" class="w-full h-12 rounded-xl font-semibold shadow-sm mt-6 text-sm">
          Sign In
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendCode, login } from '@/api/auth'

const router = useRouter()
const loading = ref(false)
const countdown = ref(0)
const form = reactive({
  phone: '13800138000',
  code: ''
})

const handleSendCode = async () => {
  if (!form.phone) return ElMessage.warning('Please enter phone number')
  try {
    await sendCode(form.phone)
    ElMessage.success('Code sent! (Check backend console)')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {}
}

const handleLogin = async () => {
  if (!form.phone || !form.code) return ElMessage.warning('Please fill in all fields')
  
  loading.value = true
  try {
    const res = await login(form)
    localStorage.setItem('token', res.token)
    localStorage.setItem('userInfo', JSON.stringify(res.user))
    ElMessage.success('Welcome back!')
    router.push('/dashboard')
  } catch (error) {} finally {
    loading.value = false
  }
}
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.bento-input .el-input__wrapper) {
  @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl h-11 px-4 transition-colors focus-within:bg-white focus-within:border-primary/50;
}
</style>