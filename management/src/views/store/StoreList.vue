<template>
  <div class="space-y-6 flex flex-col h-full">
    <div class="bg-white p-5 rounded-2xl shadow-bento flex items-center justify-between border border-slate-50 shrink-0">
      <div class="flex items-center gap-5 flex-1">
        <el-input 
          v-model="searchKeyword" 
          placeholder="Search stores by name..." 
          prefix-icon="Search" 
          class="bento-search-alt w-72" 
          clearable
          @input="handleFilter"
          @clear="handleFilter"
        />
      </div>
      <el-button @click="openDialog()" type="primary" color="#4f46e5" icon="Plus" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
        Add Store
      </el-button>
    </div>

    <div class="bg-white rounded-layout shadow-bento flex-1 flex flex-col overflow-hidden border border-slate-50">
      <div class="p-6 border-b border-slate-50 flex justify-between items-center shrink-0">
        <h2 class="text-lg font-bold text-slate-800">Store List</h2>
      </div>
      
      <div class="flex-1 overflow-auto">
        <el-table :data="filteredStores" class="admin-table">
          <el-table-column prop="storeId" label="ID" width="80" />
          <el-table-column label="Store Details" min-width="200">
            <template #default="scope">
              <p class="font-semibold text-slate-700 text-sm">{{ scope.row.storeName }}</p>
              <p class="text-xs font-medium text-slate-400 mt-1">{{ scope.row.address }}</p>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="Phone" width="150" />
          <el-table-column label="Actions" align="right" width="200">
            <template #default="scope">
              <el-button link type="primary" @click="$router.push({ name: 'StoreProducts', params: { id: scope.row.storeId } })">
                <el-icon :size="16"><Goods /></el-icon>
                <span class="ml-1 text-xs">Products</span>
              </el-button>
              <el-button link class="text-slate-400 hover:text-primary transition-colors" @click="openDialog(scope.row)">
                <el-icon :size="18"><EditPen /></el-icon>
              </el-button>
              <el-button link class="text-rose-400 hover:text-rose-600 transition-colors" @click="handleDelete(scope.row.storeId)">
                <el-icon :size="18"><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Store' : 'Add Store'" width="500px" class="bento-dialog">
      <el-form :model="form" label-position="top" class="px-2">
        <el-form-item label="Store Name">
          <el-input v-model="form.storeName" placeholder="e.g. Central Store" />
        </el-form-item>
        <el-form-item label="Address">
          <el-input v-model="form.address" placeholder="Store address" />
        </el-form-item>
        <el-form-item label="Phone">
          <el-input v-model="form.phone" placeholder="Phone number" />
        </el-form-item>
        <el-form-item label="Hours">
          <el-input v-model="form.hours" placeholder="e.g. 07:00-23:00" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="rounded-xl">Cancel</el-button>
        <el-button type="primary" color="#4f46e5" class="rounded-xl" @click="handleSave">
          {{ isEdit ? 'Update' : 'Create' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStores, addStore, updateStore, deleteStore } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const stores = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ storeName: '', address: '', phone: '', hours: '' })

const filteredStores = computed(() => {
  if (!searchKeyword.value) return stores.value
  const kw = searchKeyword.value.toLowerCase()
  return stores.value.filter(s => (s.storeName || '').toLowerCase().includes(kw) || (s.address || '').toLowerCase().includes(kw))
})

const fetchData = async () => {
  try {
    const res = await getStores()
    stores.value = res.data || []
  } catch (e) {
    ElMessage.error('Failed to load stores')
  }
}

const openDialog = (row = null) => {
  if (row) {
    isEdit.value = true
    form.value = { storeId: row.storeId, storeName: row.storeName, address: row.address, phone: row.phone, hours: row.hours }
  } else {
    isEdit.value = false
    form.value = { storeName: '', address: '', phone: '', hours: '' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await updateStore(form.value)
      ElMessage.success('Store updated')
    } else {
      await addStore(form.value)
      ElMessage.success('Store created')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('Operation failed')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('Delete this store? This will also remove its product assignments.', 'Warning', { type: 'warning' })
    await deleteStore(id)
    ElMessage.success('Store deleted')
    fetchData()
  } catch (e) { /* cancelled */ }
}

const handleFilter = () => {}

onMounted(fetchData)
</script>
