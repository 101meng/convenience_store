<template>
  <div class="space-y-6 flex flex-col h-full">
    <div class="flex justify-between items-end shrink-0">
      <div>
        <h1 class="text-2xl font-bold text-slate-800 tracking-tight">User Management</h1>
        <p class="text-slate-500 text-sm mt-1">Manage customer accounts, balances, and default addresses.</p>
      </div>
      <el-button @click="dialogVisible = true" type="primary" color="#4f46e5" icon="Plus" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
        Add User
      </el-button>
    </div>

    <div class="bg-white rounded-layout shadow-bento flex-1 flex flex-col overflow-hidden border border-slate-50">
      
      <div class="p-6 border-b border-slate-50 flex justify-between items-center shrink-0">
        <h2 class="text-lg font-bold text-slate-800">Active Users</h2>
        <div class="flex gap-2">
          <el-input 
            v-model="searchKeyword" 
            @input="handleSearch" 
            placeholder="Search nickname or phone..." 
            prefix-icon="Search" 
            class="w-64 bento-search-alt" 
            clearable 
            @clear="handleSearch" 
          />
        </div>
      </div>

      <div class="flex-1 overflow-auto">
        <el-table :data="users" style="width: 100%" class="admin-table">
          <el-table-column label="Avatar & Nickname" min-width="200">
            <template #default="scope">
              <div class="flex items-center gap-3 py-1">
                <el-avatar :size="36" :src="scope.row.avatarUrl || 'https://ui-avatars.com/api/?name=U&background=random'" class="border border-slate-100 bg-slate-50" />
                <div>
                  <p class="font-semibold text-slate-700">{{ scope.row.nickname || 'Guest User' }}</p>
                  <p class="text-[10px] text-slate-400 font-medium mt-0.5">ID: {{ scope.row.userId }}</p>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="Phone" width="180">
             <template #default="scope">
               <span class="text-slate-600 font-medium">{{ scope.row.phone }}</span>
             </template>
          </el-table-column>
          
          <el-table-column label="Default Address" min-width="250">
            <template #default="scope">
              <p class="text-xs text-slate-500 font-medium truncate max-w-[200px]">
                {{ scope.row.address || 'No address set' }}
              </p>
            </template>
          </el-table-column>
          
          <el-table-column label="Balance" width="120">
            <template #default="scope">
              <span :class="['font-bold', scope.row.balance > 0 ? 'text-slate-800' : 'text-rose-500']">
                ${{ scope.row.balance?.toFixed(2) || '0.00' }}
              </span>
            </template>
          </el-table-column>
          
          <el-table-column label="Actions" width="120" align="right">
            <template #default="scope">
              <el-button type="primary" link class="font-semibold" @click="handleRecharge(scope.row)">
                Recharge
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="p-5 border-t border-slate-50 flex justify-between items-center bg-slate-50/30 shrink-0">
        <p class="text-xs text-slate-400 font-medium">
          Showing {{ pagination.total === 0 ? 0 : (pagination.current - 1) * pagination.size + 1 }} to {{ Math.min(pagination.current * pagination.size, pagination.total) }} of {{ pagination.total }} users
        </p>
        <el-pagination 
          v-model:current-page="pagination.current" 
          v-model:page-size="pagination.size"
          :total="pagination.total" 
          layout="prev, pager, next"
          @current-change="fetchData"
          class="custom-pagination" 
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="Add New User" width="400px" class="bento-dialog">
      <el-form :model="form" label-position="top" class="mt-2 space-y-4">
        <el-form-item label="Phone Number *">
          <el-input v-model="form.phone" placeholder="e.g. 13800138000" class="bento-input-dialog" />
        </el-form-item>
        <el-form-item label="Nickname">
          <el-input v-model="form.nickname" placeholder="e.g. John Doe" class="bento-input-dialog" />
        </el-form-item>
        <el-form-item label="Default Address">
          <el-input v-model="form.address" placeholder="e.g. 123 Bento Lane" class="bento-input-dialog" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="rounded-xl font-bold h-10 border-slate-200">Cancel</el-button>
        <el-button type="primary" color="#4f46e5" @click="handleSave" class="rounded-xl font-bold h-10 px-6 shadow-sm">Save</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAdminUsers, addUser, rechargeUser } from '@/api/admin'

const users = ref([])
const searchKeyword = ref('')
const pagination = reactive({ current: 1, size: 8, total: 0 })
const dialogVisible = ref(false)
const form = reactive({ phone: '', nickname: '', address: '' })

const fetchData = async () => {
  try {
    const res = await getAdminUsers({ current: pagination.current, size: pagination.size, keyword: searchKeyword.value })
    users.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) { users.value = [] }
}

onMounted(() => { fetchData() })
const handleSearch = () => { pagination.current = 1; fetchData() }

const handleSave = async () => {
  if (!form.phone) return ElMessage.warning("Phone number is required")
  try {
    await addUser(form); ElMessage.success("User added successfully")
    dialogVisible.value = false; Object.assign(form, { phone: '', nickname: '', address: '' })
    fetchData()
  } catch (error) {}
}

// 🔥 终极充值逻辑：抛弃内存自嗨，真实写库！
const handleRecharge = (user) => {
  ElMessageBox.prompt(`Recharge for ${user.nickname || user.phone}`, 'Wallet Balance', {
    confirmButtonText: 'Confirm', 
    cancelButtonText: 'Cancel',
    inputPattern: /^\d+(\.\d{1,2})?$/, 
    inputErrorMessage: 'Invalid amount (e.g. 10.00)',
  }).then(async ({ value }) => {
    try {
      // 1. 真实请求后端修改数据库
      await rechargeUser(user.userId, parseFloat(value))
      ElMessage.success(`Successfully recharged $${value}`)
      // 2. 重新拉取最新数据，再也不怕刷新啦！
      fetchData() 
    } catch (e) {
      ElMessage.error("Recharge failed")
    }
  }).catch(() => {})
}
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.bento-search-alt .el-input__wrapper) { 
  @apply bg-slate-50/80 border-none shadow-none rounded-xl h-10 px-4; 
}
:deep(.bento-search-alt .el-input__inner) { 
  @apply font-medium text-slate-600; 
}
:deep(.bento-dialog) { 
  @apply rounded-2xl overflow-hidden; 
}
:deep(.el-dialog__header) { 
  @apply pb-0 border-none font-bold text-slate-800; 
}
:deep(.el-form-item__label) { 
  @apply font-semibold text-slate-600 pb-1; 
}
:deep(.bento-input-dialog .el-input__wrapper) { 
  @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl h-11; 
}
:deep(.custom-pagination .btn-prev), :deep(.custom-pagination .btn-next) {
  @apply bg-white border border-slate-200 rounded-lg h-8 w-8 text-slate-500 hover:text-primary transition-colors hover:border-indigo-200;
}
:deep(.custom-pagination .el-pager li) {
  @apply bg-transparent text-slate-500 font-semibold rounded-lg h-8 min-w-[32px] mx-1 transition-colors hover:text-primary;
}
:deep(.custom-pagination .el-pager li.is-active) {
  @apply bg-primary text-white font-bold;
}
</style>
