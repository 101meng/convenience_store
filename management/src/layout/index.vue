<template>
  <div class="h-screen w-full flex bg-bg-light overflow-hidden">
    <aside class="w-[260px] bg-white flex flex-col border-r border-slate-100/60 z-10">
      <div class="h-24 flex items-center px-8">
        <div class="w-9 h-9 bg-primary rounded-[10px] flex items-center justify-center shadow-md shadow-indigo-100">
          <el-icon color="white" :size="18"><Box /></el-icon>
        </div>
        <div class="ml-3">
          <h1 class="text-base font-bold text-slate-800">Bento Box</h1>
          <p class="text-[11px] text-slate-400 font-medium mt-0.5">Retail Admin</p>
        </div>
      </div>

      <nav class="flex-1 px-4 space-y-1.5 mt-2">
        <router-link v-for="item in menu" :key="item.path" :to="item.path" v-slot="{ isActive }">
          <div :class="['flex items-center px-4 py-3 rounded-xl transition-all duration-300 group', 
            isActive ? 'bg-indigo-50/70 text-primary font-semibold' : 'text-slate-500 hover:bg-slate-50 font-medium']">
            <el-icon :size="18" :class="isActive ? 'text-primary' : 'text-slate-400 group-hover:text-slate-500'">
              <component :is="item.icon" />
            </el-icon>
            <span class="ml-3.5 text-sm">{{ item.name }}</span>
          </div>
        </router-link>
      </nav>

      <div class="p-5 border-t border-slate-50 space-y-1">
        <div class="flex items-center px-4 py-3 text-slate-500 hover:bg-slate-50 rounded-xl cursor-pointer font-medium transition-colors">
          <el-icon :size="18"><Setting /></el-icon>
          <span class="ml-3.5 text-sm">Settings</span>
        </div>
        <div @click="handleLogout" class="flex items-center px-4 py-3 text-slate-500 hover:text-rose-500 hover:bg-rose-50 rounded-xl cursor-pointer font-medium transition-colors">
          <el-icon :size="18"><SwitchButton /></el-icon>
          <span class="ml-3.5 text-sm">Logout</span>
        </div>
      </div>
    </aside>

    <div class="flex-1 flex flex-col">
      <header class="h-20 bg-white/70 backdrop-blur-xl flex items-center justify-between px-10 border-b border-slate-100/60 sticky top-0 z-20">
        <div class="w-80">
          <el-input placeholder="Global Search..." prefix-icon="Search" class="bento-search" />
        </div>
        
        <div class="flex items-center space-x-6">
          <el-tooltip content="AI Stock Alerts: 3 Items Low" placement="bottom">
            <el-badge is-dot class="flex mt-1">
              <el-icon :size="20" class="text-slate-400 hover:text-rose-500 transition-colors cursor-pointer"><Warning /></el-icon>
            </el-badge>
          </el-tooltip>
          
          <div @click="aiVisible = true" class="flex items-center gap-2 bg-indigo-50 px-3 py-1.5 rounded-lg cursor-pointer hover:bg-indigo-100 transition-colors">
            <span class="text-xs font-bold text-primary">✨ AI Assistant</span>
          </div>
          
          <div class="h-6 w-px bg-slate-200 mx-2"></div>
          
          <div class="flex items-center gap-3 cursor-pointer">
             <el-avatar :size="32" :src="userInfo.avatarUrl || 'https://i.pravatar.cc/150'" class="ring-2 ring-white shadow-sm bg-slate-50" />
             <span class="text-sm font-bold text-slate-700">{{ userInfo.nickname || 'Admin' }}</span>
          </div>
        </div>
      </header>
      
      <main class="flex-1 overflow-y-auto p-10">
        <div class="max-w-7xl mx-auto">
           <router-view />
        </div>
      </main>
    </div>

    <el-dialog v-model="aiVisible" title="🤖 AI Store Assistant" width="500px" class="bento-dialog" destroy-on-close>
      <div class="flex flex-col h-[400px]">
        <div class="flex-1 overflow-y-auto p-2 space-y-4 mb-4">
          <div v-for="(msg, i) in chatHistory" :key="i" :class="['flex', msg.role === 'user' ? 'justify-end' : 'justify-start']">
            <div :class="['max-w-[85%] p-3 rounded-2xl text-sm leading-relaxed whitespace-pre-wrap', 
                 msg.role === 'user' ? 'bg-primary text-white rounded-br-none' : 'bg-slate-100 text-slate-700 rounded-bl-none']">
              {{ msg.content }}
            </div>
          </div>
          <div v-if="aiLoading" class="flex justify-start">
            <div class="bg-slate-100 text-slate-500 p-3 rounded-2xl rounded-bl-none text-sm flex items-center gap-2">
              <el-icon class="is-loading"><Loading /></el-icon> Thinking...
            </div>
          </div>
        </div>
        
        <div class="flex gap-2 shrink-0 border-t border-slate-50 pt-4">
          <el-input 
            v-model="aiPrompt" 
            placeholder="Draft an email, analyze sales..." 
            class="bento-input-dialog flex-1"
            @keyup.enter="handleAskAi"
            :disabled="aiLoading"
          />
          <el-button type="primary" color="#4f46e5" class="h-11 rounded-xl font-bold shadow-sm" @click="handleAskAi" :loading="aiLoading">
            Send
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { chatWithAi } from '@/api/admin' // 引入 AI 接口

const userInfo = ref({})
const menu = [
  { name: 'Dashboard', path: '/dashboard', icon: 'DataBoard' },
  { name: 'Products', path: '/products', icon: 'Goods' },
  { name: 'Categories', path: '/categories', icon: 'CopyDocument' },
  { name: 'Orders', path: '/orders', icon: 'ShoppingCart' },
  { name: 'Banners', path: '/banners', icon: 'Picture' },
  { name: 'Users', path: '/users', icon: 'User' },
]

// AI 对话逻辑
const aiVisible = ref(false)
const aiPrompt = ref('')
const aiLoading = ref(false)
const chatHistory = ref([
  { role: 'ai', content: "Hello! I'm powered by LongCat AI. I can help you analyze sales trends, draft marketing emails, or suggest inventory restocks. What do you need help with today?" }
])

const handleAskAi = async () => {
  if (!aiPrompt.value.trim() || aiLoading.value) return
  
  const userText = aiPrompt.value
  chatHistory.value.push({ role: 'user', content: userText })
  aiPrompt.value = ''
  aiLoading.value = true
  
  try {
    const responseText = await chatWithAi(userText)
    chatHistory.value.push({ role: 'ai', content: responseText || 'Oops, empty response.' })
  } catch (e) {
    chatHistory.value.push({ role: 'ai', content: 'Network error. Please try again.' })
  } finally {
    aiLoading.value = false
  }
}

onMounted(() => {
  const storedUser = localStorage.getItem('userInfo')
  if (storedUser) {
    userInfo.value = JSON.parse(storedUser)
  }
})

const handleLogout = () => {
  localStorage.clear()
  window.location.href = '/login'
}
</script>

<style scoped>
@reference "../assets/tailwind.css";
:deep(.bento-search .el-input__wrapper) { @apply bg-slate-100/50 border-none shadow-none rounded-xl h-10 px-4; }
:deep(.bento-search .el-input__inner) { @apply font-medium text-slate-600; }
:deep(.bento-dialog) { @apply rounded-2xl overflow-hidden; }
:deep(.el-dialog__header) { @apply pb-0 border-none font-bold text-slate-800; }
:deep(.bento-input-dialog .el-input__wrapper) { @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl h-11; }
</style>