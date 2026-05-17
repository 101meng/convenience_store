<template>
  <div class="space-y-6">
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-2xl font-bold text-slate-800 tracking-tight">Banner Management</h2>
        <p class="text-slate-500 text-sm mt-1">Manage promotional banners across the storefront.</p>
      </div>
      <el-button @click="dialogVisible = true" type="primary" color="#4f46e5" icon="Plus" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
        New Banner
      </el-button>
    </div>

    <div class="bg-white rounded-layout shadow-bento overflow-hidden border border-slate-50">
      <div class="p-6 border-b border-slate-50 flex justify-between items-center">
        <h3 class="text-lg font-bold text-slate-800">Active Campaigns</h3>
        <div class="flex gap-4 items-center">
          <el-input v-model="searchKeyword" placeholder="Filter by link..." prefix-icon="Search" class="w-48 bento-search-alt" clearable />
          <el-button @click="sortDesc = !sortDesc" :icon="sortDesc ? 'SortDown' : 'SortUp'" class="rounded-lg border-slate-100 text-slate-500" />
        </div>
      </div>

      <div class="p-4 space-y-3">
        <div v-for="b in filteredBanners" :key="b.id" 
          class="flex items-center gap-8 p-4 rounded-2xl hover:bg-slate-50/50 transition-all duration-300 group border border-transparent hover:border-slate-100">
          
          <div class="w-48 h-24 rounded-xl bg-slate-50 overflow-hidden border border-slate-100 flex items-center justify-center">
             <el-image :src="formatImageUrl(b.imageUrl)" fit="cover" class="w-full h-full group-hover:scale-[1.02] transition-transform duration-500" />
          </div>

          <div class="flex-1">
            <div class="flex items-center gap-2 mb-1.5">
              <el-icon class="text-slate-400" :size="14"><Link /></el-icon>
              <span class="font-semibold text-sm text-slate-700">{{ b.linkUrl }}</span>
            </div>
            <p class="text-xs font-medium text-slate-400 uppercase tracking-wide">ID: {{ b.id }}</p>
          </div>

          <div class="flex items-center gap-8">
            <div class="flex flex-col items-center gap-1.5">
              <span class="text-[10px] font-semibold text-slate-400 uppercase">Sort Order</span>
              <div class="flex items-center gap-2 font-semibold text-slate-600 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-100">
                 {{ b.sortOrder }} 
                 <div class="flex flex-col cursor-pointer text-slate-400">
                   <el-icon @click="handleSortChange(b, 1)" class="hover:text-primary"><ArrowUp /></el-icon>
                   <el-icon @click="handleSortChange(b, -1)" class="hover:text-primary"><ArrowDown /></el-icon>
                 </div>
              </div>
            </div>
            <el-switch v-model="b.active" color="#4f46e5" @change="() => handleStatusChange(b)" />
            <el-button link type="danger" class="text-rose-400 hover:text-rose-600" @click="handleDelete(b.id)">
              <el-icon :size="18"><Delete /></el-icon>
            </el-button>
          </div>
        </div>
        
        <div v-if="filteredBanners.length === 0" class="py-10 text-center text-slate-400 font-medium text-sm">
          No banners found. 
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="Add New Banner" width="450px" class="bento-dialog">
      <el-form :model="form" label-position="top" class="mt-2 space-y-4">
        <el-form-item label="Image URL">
          <el-input v-model="form.imageUrl" placeholder="http://..." class="bento-input-dialog" />
        </el-form-item>
        <el-form-item label="Redirect Link">
          <el-input v-model="form.linkUrl" placeholder="e.g. /category/1" class="bento-input-dialog" />
        </el-form-item>
        <div class="flex gap-4">
          <el-form-item label="Sort Order" class="flex-1">
            <el-input-number v-model="form.sortOrder" :min="0" :controls="false" class="w-full bento-input-dialog" />
          </el-form-item>
          <el-form-item label="Status" class="flex-1">
             <div class="flex items-center h-11"><el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" color="#4f46e5" /></div>
          </el-form-item>
        </div>
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
import { getAdminBanners, updateBannerStatus, deleteBanner, addBanner, updateBanner } from '@/api/admin'

const banners = ref([])
const searchKeyword = ref('')
const sortDesc = ref(true)

const dialogVisible = ref(false)
const form = reactive({ imageUrl: '', linkUrl: '', sortOrder: 0, isActive: 1 })

const formatImageUrl = (url) => {
  if (!url) return ''
  return url.replace('10.0.2.2', 'localhost')
}

const fetchBanners = async () => {
  try {
    const data = await getAdminBanners()
    banners.value = data.map(b => ({ ...b, active: b.isActive === 1 }))
  } catch (error) {}
}

onMounted(() => { fetchBanners() })

// 🔥 纯前端实现实时过滤与倒序正序
const filteredBanners = computed(() => {
  let result = banners.value
  if (searchKeyword.value) {
    result = result.filter(b => (b.linkUrl && b.linkUrl.includes(searchKeyword.value)) || (b.imageUrl && b.imageUrl.includes(searchKeyword.value)))
  }
  return result.sort((a, b) => sortDesc.value ? b.sortOrder - a.sortOrder : a.sortOrder - b.sortOrder)
})

const handleStatusChange = async (banner) => {
  try {
    await updateBannerStatus(banner.id, banner.active)
    ElMessage.success('Status updated successfully')
  } catch (error) { banner.active = !banner.active }
}

// 🔥 处理排序权重的点击
const handleSortChange = async (banner, delta) => {
  try {
    banner.sortOrder += delta
    await updateBanner({ id: banner.id, sortOrder: banner.sortOrder })
    fetchBanners()
  } catch (error) { ElMessage.error("Failed to update sort order") }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('Are you sure you want to delete this banner?', 'Warning', { confirmButtonText: 'Delete', cancelButtonText: 'Cancel', type: 'warning' })
  .then(async () => {
    try { await deleteBanner(id); ElMessage.success('Banner deleted'); fetchBanners() } catch (error) {}
  }).catch(() => {})
}

// 🔥 处理表单保存
const handleSave = async () => {
  if(!form.imageUrl) return ElMessage.warning("Image URL is required")
  try {
    await addBanner(form)
    ElMessage.success("Banner added successfully")
    dialogVisible.value = false
    Object.assign(form, { imageUrl: '', linkUrl: '', sortOrder: 0, isActive: 1 })
    fetchBanners()
  } catch (error) {}
}
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.bento-search-alt .el-input__wrapper) { @apply bg-slate-50/80 border-none shadow-none rounded-xl h-10 px-4; }
:deep(.bento-dialog) { @apply rounded-2xl overflow-hidden; }
:deep(.el-dialog__header) { @apply pb-0 border-none font-bold text-slate-800; }
:deep(.el-form-item__label) { @apply font-semibold text-slate-600 pb-1; }
:deep(.bento-input-dialog .el-input__wrapper) { @apply bg-slate-50 border border-slate-100 shadow-none rounded-xl h-11; }
</style>
