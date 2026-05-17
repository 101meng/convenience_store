<template>
  <div class="space-y-6 flex flex-col h-full">
    <!-- Header -->
    <div class="bg-white p-5 rounded-2xl shadow-bento flex items-center justify-between border border-slate-50 shrink-0">
      <div class="flex items-center gap-4">
        <el-button @click="$router.back()" text class="text-slate-400 hover:text-slate-600">
          <el-icon :size="20"><ArrowLeft /></el-icon>
        </el-button>
        <h2 class="text-lg font-bold text-slate-800">
          Products for {{ storeName }}
        </h2>
      </div>
      <el-button @click="openAssignDialog()" type="primary" color="#4f46e5" icon="Plus" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
        Assign Product
      </el-button>
    </div>

    <!-- Product Table -->
    <div class="bg-white rounded-layout shadow-bento flex-1 flex flex-col overflow-hidden border border-slate-50">
      <div class="flex-1 overflow-auto">
        <el-table :data="storeProducts" class="admin-table">
          <el-table-column label="Image" width="100">
            <template #default="scope">
              <el-image :src="formatImage(scope.row.image_url)" class="w-12 h-12 bg-slate-50 rounded-xl border border-slate-100" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column label="Product" min-width="200">
            <template #default="scope">
              <p class="font-semibold text-slate-700 text-sm">{{ scope.row.product_name }}</p>
              <p class="text-xs font-medium text-slate-400 mt-1">PID: {{ scope.row.product_id }}</p>
            </template>
          </el-table-column>
          <el-table-column label="Price" width="150">
            <template #default="scope">
              <el-input-number 
                v-model="scope.row.price" 
                :min="0" :precision="2" :step="0.5"
                size="small" 
                @change="(val) => updatePrice(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="Stock" width="150">
            <template #default="scope">
              <el-input-number 
                v-model="scope.row.stock" 
                :min="0" 
                size="small"
                @change="(val) => updateStock(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="Status" width="120">
            <template #default="scope">
              <el-switch 
                v-model="scope.row.status" 
                :active-value="1" :inactive-value="0"
                active-color="#4f46e5"
                @change="(val) => toggleStatus(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="Actions" align="right" width="100">
            <template #default="scope">
              <el-button link class="text-rose-400 hover:text-rose-600 transition-colors" @click="handleRemove(scope.row.id)">
                <el-icon :size="18"><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="p-5 border-t border-slate-50 bg-slate-50/30 shrink-0">
        <p class="text-xs text-slate-400 font-medium">
          {{ storeProducts.length }} product(s) assigned to this store
        </p>
      </div>
    </div>

    <!-- Assign Product Dialog -->
    <el-dialog v-model="assignVisible" title="Assign Product" width="500px" class="bento-dialog">
      <el-form :model="assignForm" label-position="top" class="px-2">
        <el-form-item label="Product">
          <el-select v-model="assignForm.productId" placeholder="Select a product" filterable class="w-full">
            <el-option v-for="p in allProducts" :key="p.productId" :label="p.name" :value="p.productId" />
          </el-select>
        </el-form-item>
        <el-form-item label="Price">
          <el-input-number v-model="assignForm.price" :min="0" :precision="2" :step="0.5" class="w-full" />
        </el-form-item>
        <el-form-item label="Stock">
          <el-input-number v-model="assignForm.stock" :min="0" class="w-full" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false" class="rounded-xl">Cancel</el-button>
        <el-button type="primary" color="#4f46e5" class="rounded-xl" @click="handleAssign">
          Assign
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStoreProducts, addStoreProduct, updateStoreProduct, deleteStoreProduct } from '../../api/admin'
import { getStores } from '../../api/admin'
import { getAdminProducts } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const storeId = Number(route.params.id)

const storeName = ref('')
const storeProducts = ref([])
const allProducts = ref([])
const assignVisible = ref(false)
const assignForm = ref({ productId: null, price: 0, stock: 0 })

const IMG_BASE = 'http://localhost:8088/'
const formatImage = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return IMG_BASE + url.replace(/^\//, '')
}

const fetchStoreProducts = async () => {
  try {
    const res = await getStoreProducts(storeId)
    storeProducts.value = (Array.isArray(res) ? res : []).map(sp => ({
      ...sp,
      status: sp.status == null ? 1 : sp.status,
      stock: sp.stock == null ? 0 : sp.stock,
      price: sp.price == null ? 0 : sp.price
    }))
  } catch (e) {
    ElMessage.error('Failed to load store products')
  }
}

const fetchAllProducts = async () => {
  try {
    const res = await getAdminProducts({ current: 1, size: 500 })
    allProducts.value = res.records || []
  } catch (e) { /* ignore */ }
}

const updatePrice = async (row) => {
  try {
    await updateStoreProduct({ id: row.id, storeId: storeId, productId: row.product_id, price: row.price, stock: row.stock, status: row.status })
    ElMessage.success('Price updated')
  } catch (e) { ElMessage.error('Update failed') }
}

const updateStock = async (row) => {
  try {
    await updateStoreProduct({ id: row.id, storeId: storeId, productId: row.product_id, price: row.price, stock: row.stock, status: row.status })
    ElMessage.success('Stock updated')
  } catch (e) { ElMessage.error('Update failed') }
}

const toggleStatus = async (row) => {
  try {
    await updateStoreProduct({ id: row.id, storeId: storeId, productId: row.product_id, price: row.price, stock: row.stock, status: row.status })
    ElMessage.success(row.status === 1 ? 'Product enabled' : 'Product disabled')
  } catch (e) { ElMessage.error('Update failed') }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('Remove this product from the store?', 'Confirm', { type: 'warning' })
    await deleteStoreProduct(id)
    ElMessage.success('Removed')
    fetchStoreProducts()
  } catch (e) { /* cancelled */ }
}

const openAssignDialog = async () => {
  await fetchAllProducts()
  assignForm.value = { productId: null, price: 9.99, stock: 100 }
  assignVisible.value = true
}

const handleAssign = async () => {
  if (!assignForm.value.productId) {
    ElMessage.warning('Please select a product')
    return
  }
  try {
    await addStoreProduct({
      storeId: storeId,
      productId: assignForm.value.productId,
      price: assignForm.value.price,
      stock: assignForm.value.stock,
      status: 1
    })
    ElMessage.success('Product assigned')
    assignVisible.value = false
    fetchStoreProducts()
  } catch (e) {
    ElMessage.error('Assignment failed')
  }
}

onMounted(async () => {
  const storesRes = await getStores()
  const stores = Array.isArray(storesRes) ? storesRes : []
  const store = stores.find(s => s.storeId === storeId)
  storeName.value = store ? store.storeName : 'Store #' + storeId
  fetchStoreProducts()
})
</script>
