<template>
  <div class="flex gap-8 items-start h-full">
    <div class="flex-1 space-y-6 flex flex-col h-[calc(100vh-120px)]">
      <div class="flex justify-between items-end shrink-0">
        <div>
          <h2 class="text-2xl font-bold text-slate-800 tracking-tight">Categories</h2>
          <p class="text-slate-500 text-sm mt-1">Manage and organize your product catalog.</p>
        </div>
        <el-button @click="dialogVisible = true" type="primary" color="#4f46e5" icon="Plus" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
          Add Category
        </el-button>
      </div>

      <div class="bg-white rounded-layout shadow-bento flex-1 flex flex-col overflow-hidden border border-slate-50">
        <div class="p-6 border-b border-slate-50 flex justify-between items-center shrink-0">
          <h3 class="text-lg font-bold text-slate-800">All Categories</h3>
          <el-input v-model="searchCat" placeholder="Find category..." prefix-icon="Search" class="w-64 bento-search-alt" clearable />
        </div>
        
        <div class="flex-1 overflow-auto">
          <el-table :data="pagedCategories" class="admin-table">
            <el-table-column prop="categoryId" label="ID" width="100">
              <template #default="scope"><span class="text-slate-500 font-bold">#{{ scope.row.categoryId }}</span></template>
            </el-table-column>
            
            <el-table-column label="Icon" width="100">
              <template #default="scope">
                <div class="w-10 h-10 rounded-xl bg-slate-50 flex items-center justify-center border border-slate-100 text-xl shadow-inner">
                  {{ getCategoryEmoji(scope.row.categoryName) }}
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="categoryName" label="Category Name">
              <template #default="scope">
                <span class="font-semibold text-slate-700">{{ scope.row.categoryName }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="Actions" align="right">
              <template #default="scope">
                <el-button link type="danger" class="text-rose-400 hover:text-rose-600 transition-colors" @click="handleDelete(scope.row.categoryId)">
                  <el-icon :size="18"><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="p-5 border-t border-slate-50 flex justify-between items-center bg-slate-50/30 shrink-0">
          <p class="text-xs text-slate-400 font-medium">
            Showing {{ pagination.total === 0 ? 0 : (pagination.current - 1) * pagination.size + 1 }} to {{ Math.min(pagination.current * pagination.size, pagination.total) }} of {{ pagination.total }} categories
          </p>
          <el-pagination 
            v-model:current-page="pagination.current" 
            v-model:page-size="pagination.size"
            :total="pagination.total" 
            layout="prev, pager, next"
            class="custom-pagination" 
          />
        </div>
      </div>
    </div>

    <div class="w-80 bg-white p-7 rounded-layout shadow-bento border border-slate-50 sticky top-0 shrink-0">
      <p class="text-xs font-bold text-slate-800 mb-5">Category Insights</p>
      <div class="p-5 bg-slate-50/50 rounded-2xl border border-slate-100">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-8 h-8 bg-white rounded-lg flex items-center justify-center shadow-sm text-primary">
            <el-icon :size="16"><TrendCharts /></el-icon>
          </div>
          <p class="text-xs font-semibold text-slate-500">Top Category</p>
        </div>
        <p class="text-lg font-bold text-slate-800" v-if="categories.length > 0">
          {{ getCategoryEmoji(categories[0].categoryName) }} {{ categories[0].categoryName }}
        </p>
        <p class="text-xs text-slate-500 font-medium mt-1.5">Your most popular category.</p>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="Add New Category" width="400px" class="bento-dialog">
      <el-form label-position="top" class="mt-2">
        <el-form-item label="Category Name">
          <el-input v-model="newCatName" placeholder="e.g. Ice Cream" class="bento-input-dialog" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" class="rounded-xl font-bold h-10">Cancel</el-button>
        <el-button type="primary" color="#4f46e5" @click="handleSave" class="rounded-xl font-bold h-10 px-6">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories } from '@/api/store'
import { addCategory, deleteCategory } from '@/api/admin' // 引入新的API

const categories = ref([])
const searchCat = ref('')
const pagination = reactive({ current: 1, size: 5, total: 0 })

// 弹窗状态
const dialogVisible = ref(false)
const newCatName = ref('')

const getCategoryEmoji = (name) => {
  if (!name) return '📦'
  const lowerName = name.toLowerCase()
  if (lowerName.includes('bento') || lowerName.includes('fresh')) return '🍱'
  if (lowerName.includes('snack')) return '🍟'
  if (lowerName.includes('drink') || lowerName.includes('beverage')) return '🥤'
  if (lowerName.includes('bakery')) return '🥐'
  if (lowerName.includes('dessert') || lowerName.includes('ice')) return '🍰'
  return '🏷️'
}

const fetchCategories = async () => {
  try {
    categories.value = await getCategories()
  } catch (error) {}
}

// 🔥 前端模糊搜索 + 分页切片计算
const pagedCategories = computed(() => {
  let result = categories.value
  if (searchCat.value) {
    result = result.filter(c => c.categoryName.toLowerCase().includes(searchCat.value.toLowerCase()))
  }
  pagination.total = result.length
  
  const start = (pagination.current - 1) * pagination.size
  return result.slice(start, start + pagination.size)
})

onMounted(() => {
  fetchCategories()
})

// 🔥 真实新增分类
const handleSave = async () => {
  if (!newCatName.value) return ElMessage.warning("Please enter category name")
  try {
    await addCategory({ categoryName: newCatName.value })
    ElMessage.success("Category added!")
    dialogVisible.value = false
    newCatName.value = ''
    fetchCategories()
  } catch (e) {}
}

// 🔥 真实删除分类（附带商品校验保护提示）
const handleDelete = (id) => {
  ElMessageBox.confirm('Are you sure you want to delete this category?', 'Warning', {
    confirmButtonText: 'Delete', cancelButtonText: 'Cancel', type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(id)
      ElMessage.success('Category deleted successfully')
      fetchCategories()
    } catch (error) {
      // 当后端因为分类下有商品而返回失败时，友好的用户提示
      ElMessage.error('Cannot delete: Please ensure no products are under this category first.')
    }
  }).catch(() => {})
}
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.bento-search-alt .el-input__wrapper) {
  @apply bg-slate-50/80 border-none shadow-none rounded-xl h-10 px-4;
}
:deep(.bento-dialog) { @apply rounded-2xl overflow-hidden; }
:deep(.el-dialog__header) { @apply pb-0 border-none font-bold text-slate-800; }
:deep(.el-form-item__label) { @apply font-semibold text-slate-600 pb-1; }
:deep(.bento-input-dialog .el-input__wrapper) { @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl h-11; }

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