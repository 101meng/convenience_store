<template>
  <div class="space-y-6 flex flex-col h-full">
    <div class="bg-white p-5 rounded-2xl shadow-bento flex items-center justify-between border border-slate-50 shrink-0">
      <div class="flex items-center gap-5 flex-1 max-w-3xl">
        <el-input 
          v-model="searchKeyword" 
          placeholder="Search products by name..." 
          prefix-icon="Search" 
          class="bento-search-alt w-72" 
          clearable
          @input="handleFilter"
          @clear="handleFilter"
        />
        
        <el-select v-model="selectedCategory" placeholder="All Categories" class="bento-select w-48" clearable @change="handleFilter">
           <el-option v-for="c in categories" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
        </el-select>
        
        <div class="flex items-center gap-3 ml-2">
          <span class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Flash Sale</span>
          <el-switch v-model="flashOnly" color="#4f46e5" @change="handleFilter" />
        </div>
      </div>
      <el-button v-if="isBrandAdmin" @click="openDialog()" type="primary" color="#4f46e5" icon="Plus" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
        Add Product
      </el-button>
    </div>

    <div class="bg-white rounded-layout shadow-bento flex-1 flex flex-col overflow-hidden border border-slate-50">
      <div class="p-6 border-b border-slate-50 flex justify-between items-center shrink-0">
        <h2 class="text-lg font-bold text-slate-800">Products List</h2>
      </div>
      
      <div class="flex-1 overflow-auto">
        <el-table :data="filteredProducts" class="admin-table">
          <el-table-column label="Image" width="100">
            <template #default="scope">
               <el-image :src="formatImageUrl(scope.row.imageUrl)" class="w-12 h-12 bg-slate-50 rounded-xl border border-slate-100" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column label="Product Details" min-width="250">
            <template #default="scope">
              <p class="font-semibold text-slate-700 text-sm">{{ scope.row.name }}</p>
              <p class="text-xs font-medium text-slate-400 mt-1">ID: {{ scope.row.productId }}</p>
            </template>
          </el-table-column>
          <el-table-column label="Description" prop="description" show-overflow-tooltip min-width="200" />
          <el-table-column label="Price" width="150">
            <template #default="scope">
              <p class="font-bold text-slate-800">${{ scope.row.originalPrice ?? '0.00' }}</p>
            </template>
          </el-table-column>
          <el-table-column label="Status" width="150">
            <template #default="scope">
              <div v-if="scope.row.isFlashSale === 1" class="inline-flex items-center px-2.5 py-1 bg-rose-50 text-rose-600 rounded-md text-xs font-semibold">⚡ Flash Sale</div>
              <div v-else class="inline-flex items-center px-2.5 py-1 bg-slate-50 text-slate-500 rounded-md text-xs font-semibold">Standard</div>
            </template>
          </el-table-column>
          <el-table-column v-if="isBrandAdmin" label="Actions" align="right" width="120">
            <template #default="scope">
              <el-button link class="text-slate-400 hover:text-primary transition-colors" @click="openDialog(scope.row)"><el-icon :size="18"><EditPen /></el-icon></el-button>
              <el-button link class="text-rose-400 hover:text-rose-600 transition-colors" @click="handleDelete(scope.row.productId)"><el-icon :size="18"><Delete /></el-icon></el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="p-5 border-t border-slate-50 flex justify-between items-center bg-slate-50/30 shrink-0">
        <p class="text-xs text-slate-400 font-medium">
          Showing {{ pagination.total === 0 ? 0 : (pagination.current - 1) * pagination.size + 1 }} to {{ Math.min(pagination.current * pagination.size, pagination.total) }} of {{ pagination.total }} results
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

    <el-dialog v-model="dialogVisible" :title="form.productId ? 'Edit Product' : 'Add New Product'" width="500px" destroy-on-close class="bento-dialog">
      <el-form :model="form" label-position="top" class="space-y-4 mt-2">
        <el-form-item label="Product Name">
          <el-input v-model="form.name" placeholder="Enter product name" class="bento-input-dialog" />
        </el-form-item>
        <div class="flex gap-4">
          <el-form-item label="Category" class="flex-1">
            <el-select v-model="form.categoryId" placeholder="Select" class="w-full bento-select-dialog">
              <el-option v-for="c in categories" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
            </el-select>
          </el-form-item>
          <el-form-item label="Guide Price ($)" class="w-32">
            <el-input-number v-model="form.originalPrice" :precision="2" :step="0.1" :min="0" :controls="false" class="w-full bento-input-dialog" />
          </el-form-item>
        </div>
        <el-form-item label="Image URL">
          <el-input v-model="form.imageUrl" placeholder="http://..." class="bento-input-dialog" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="Brief description..." class="bento-textarea" />
        </el-form-item>
        <el-form-item>
          <div class="flex items-center gap-3 bg-slate-50 px-4 py-3 rounded-xl border border-slate-100 w-full">
            <span class="text-sm font-semibold text-slate-700">Set as Flash Sale</span>
            <el-switch v-model="form.isFlashSale" :active-value="1" :inactive-value="0" color="#4f46e5" class="ml-auto" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex gap-3 justify-end mt-4">
          <el-button @click="dialogVisible = false" class="h-10 rounded-xl font-semibold border-slate-200">Cancel</el-button>
          <el-button type="primary" color="#4f46e5" @click="handleSave" class="h-10 rounded-xl font-semibold shadow-sm px-6">Save</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories } from '@/api/store'
import { getAdminProducts, addProduct, updateProduct, deleteProduct } from '@/api/admin'
import { loadAdminProfile } from '@/utils/adminSession'

const flashOnly = ref(false)
const products = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const adminProfile = loadAdminProfile() || {}
const isBrandAdmin = computed(() => adminProfile.role === 'brand_admin')

const searchKeyword = ref('')
const pagination = reactive({ current: 1, size: 5, total: 0 })

const dialogVisible = ref(false)
const form = reactive({
  productId: null, categoryId: null, name: '', originalPrice: 0, imageUrl: '', description: '', isFlashSale: 0
})

const formatImageUrl = (url) => {
  if (!url) return ''
  return url.replace('10.0.2.2', 'localhost')
}

const fetchData = async () => {
  try {
    const res = await getAdminProducts({
      current: pagination.current,
      size: pagination.size,
      categoryId: selectedCategory.value,
      keyword: searchKeyword.value 
    })
    products.value = res.records || []
    pagination.total = res.total || 0
  } catch (error) {
    products.value = [] 
  }
}

onMounted(async () => {
  categories.value = await getCategories()
  fetchData()
})

const handleFilter = () => {
  pagination.current = 1
  fetchData()
}

const filteredProducts = computed(() => {
  if (flashOnly.value) return products.value.filter(p => p.isFlashSale === 1)
  return products.value
})

const openDialog = (row) => {
  if (!isBrandAdmin.value) return
  if (row) {
    Object.assign(form, {
      productId: row.productId,
      categoryId: row.categoryId,
      name: row.name,
      originalPrice: row.originalPrice ?? 0,
      imageUrl: row.imageUrl,
      description: row.description,
      isFlashSale: row.isFlashSale ?? 0
    })
  } else {
    Object.assign(form, { productId: null, categoryId: null, name: '', originalPrice: 0, imageUrl: '', description: '', isFlashSale: 0 })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!isBrandAdmin.value) return
  if (!form.name || !form.originalPrice) return ElMessage.warning('Please fill out the required fields.')
  try {
    if (form.productId) {
      await updateProduct(form)
      ElMessage.success('Product updated successfully')
    } else {
      await addProduct(form)
      ElMessage.success('Product added successfully')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (id) => {
  if (!isBrandAdmin.value) return
  ElMessageBox.confirm('Are you sure you want to delete this product?', 'Warning', {
    confirmButtonText: 'Delete', cancelButtonText: 'Cancel', type: 'warning'
  }).then(async () => {
    try {
      await deleteProduct(id)
      ElMessage.success('Product deleted successfully')
      if (products.value.length === 1 && pagination.current > 1) {
        pagination.current--
      }
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.bento-search-alt .el-input__wrapper), :deep(.bento-select .el-input__wrapper) {
  @apply bg-slate-50/80 border-none shadow-none rounded-xl h-10 px-4;
}
:deep(.bento-search-alt .el-input__inner), :deep(.bento-select .el-input__inner) {
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
:deep(.bento-input-dialog .el-input__wrapper), :deep(.bento-select-dialog .el-input__wrapper) {
  @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl h-11;
}
:deep(.bento-textarea .el-textarea__inner) {
  @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl p-3 resize-none font-medium text-slate-700;
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
