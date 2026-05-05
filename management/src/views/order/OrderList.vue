<template>
  <div class="flex gap-8 h-full">
    <div class="flex-1 space-y-6 flex flex-col h-[calc(100vh-120px)]">
      <div class="flex justify-between items-end shrink-0">
        <div>
          <h2 class="text-2xl font-bold text-slate-800 tracking-tight">Order Management</h2>
          <p class="text-slate-500 text-sm mt-1">Review, filter, and process incoming orders.</p>
        </div>
        <el-button @click="handleExport" color="#4f46e5" icon="Download" class="h-10 px-6 rounded-xl font-semibold shadow-sm">
          Export CSV
        </el-button>
      </div>

      <div class="bg-white p-4 rounded-2xl shadow-bento flex items-center justify-between border border-slate-50 shrink-0">
        <div class="flex gap-2">
          <el-button v-for="s in ['All', 'COMPLETED', 'DELIVERING', 'PENDING']" :key="s"
            :type="status === s ? 'primary' : ''" :plain="status !== s"
            class="rounded-lg font-medium px-5 border-none bg-slate-50" @click="handleStatusChange(s)">
            {{ s === 'All' ? s : (s.charAt(0) + s.slice(1).toLowerCase()) }}
          </el-button>
        </div>
        <el-date-picker 
          v-model="dateRange" 
          type="daterange" 
          range-separator="-" 
          start-placeholder="Start Date" 
          end-placeholder="End Date" 
          class="bento-date-picker" 
          @change="handleDateChange"
        />
      </div>

      <div class="bg-white rounded-layout shadow-bento border border-slate-50 flex-1 flex flex-col overflow-hidden">
        <div class="flex-1 overflow-auto">
          <el-table :data="orders" class="admin-table" @row-click="o => selected = o" :row-class-name="({row}) => [row.orderId === selected?.orderId ? 'bg-indigo-50/50' : '', 'cursor-pointer hover:bg-slate-50/50']">
            <el-table-column prop="orderSn" label="Order ID" width="200">
              <template #default="scope"><span class="font-semibold text-slate-700">{{ scope.row.orderSn }}</span></template>
            </el-table-column>
            <el-table-column label="Amount">
              <template #default="scope"><span class="font-semibold text-slate-800">${{ scope.row.actualAmount }}</span></template>
            </el-table-column>
            <el-table-column label="Type">
              <template #default="scope"><span class="text-slate-600 font-medium capitalize">{{ scope.row.orderType }}</span></template>
            </el-table-column>
            <el-table-column label="Status">
              <template #default="scope">
                <span :class="['px-3 py-1 rounded-md text-xs font-semibold', 
                  scope.row.status === 'COMPLETED' ? 'bg-emerald-50 text-emerald-600' : 
                  scope.row.status === 'DELIVERING' ? 'bg-blue-50 text-blue-600' : 'bg-amber-50 text-amber-600']">
                  {{ scope.row.status }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="p-5 border-t border-slate-50 flex justify-between items-center bg-slate-50/30 shrink-0">
          <p class="text-xs text-slate-400 font-medium">
            Showing {{ pagination.total === 0 ? 0 : (pagination.current - 1) * pagination.size + 1 }} to {{ Math.min(pagination.current * pagination.size, pagination.total) }} of {{ pagination.total }} orders
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
    </div>

    <div class="w-[400px] bg-white rounded-layout shadow-[0_0_40px_rgba(0,0,0,0.05)] border border-slate-100 p-8 flex flex-col sticky top-0 h-[calc(100vh-120px)]">
      <template v-if="selected">
        <div class="flex justify-between items-start mb-8 pb-6 border-b border-slate-50">
          <div>
            <h2 class="text-xl font-bold text-slate-800">#{{ selected.orderSn.substring(9) }}</h2>
            <p class="text-xs text-slate-400 mt-1">{{ selected.createdAt }}</p>
          </div>
          <div :class="['px-2.5 py-1 rounded-md text-xs font-semibold', 
               selected.status === 'COMPLETED' ? 'bg-emerald-50 text-emerald-600' : 
               selected.status === 'DELIVERING' ? 'bg-blue-50 text-blue-600' : 'bg-amber-50 text-amber-600']">
            {{ selected.status }}
          </div>
        </div>

        <section class="mb-8">
          <p class="text-xs font-semibold text-slate-800 mb-3">Delivery Information</p>
          <div class="bg-slate-50/50 p-4 rounded-xl text-sm text-slate-600 leading-relaxed border border-slate-100">
             {{ selected.deliveryAddress || 'Store Pickup' }}
          </div>
        </section>

        <section class="flex-1 overflow-y-auto pr-2">
          <p class="text-xs font-semibold text-slate-800 mb-4">Items Ordered</p>
          <div class="space-y-4">
            <div v-for="item in selected.items" :key="item.productId" class="flex gap-4 items-center">
              <el-image 
                :src="formatImageUrl(item.imageUrl || item.image || item.mainImage || item.productImage)" 
                class="w-16 h-16 bg-slate-50 rounded-xl border border-slate-100 shrink-0" 
                fit="cover" 
              />
              <div class="flex-1">
                <p class="font-semibold text-slate-700 text-sm line-clamp-1">{{ item.name }}</p>
                <p class="text-slate-500 text-xs mt-1">Qty: {{ item.quantity }}</p>
              </div>
              <p class="text-sm font-bold text-slate-800">${{ item.priceAtTime }}</p>
            </div>
          </div>
        </section>
        
        <div class="mt-6 pt-6 border-t border-slate-50">
            <el-button v-if="selected.status !== 'COMPLETED'" type="primary" color="#4f46e5" class="w-full h-12 rounded-xl font-semibold shadow-sm" @click="handleProcessOrder">
              {{ selected.status === 'PENDING' ? 'Dispatch Order' : 'Mark as Completed' }}
            </el-button>
            <div v-else class="text-center text-sm font-bold text-emerald-500 py-2">
              <el-icon class="mr-1"><Select /></el-icon> Order Completed
            </div>
        </div>
      </template>
      
      <div v-else class="h-full flex flex-col items-center justify-center text-slate-400">
        <el-icon :size="48" class="mb-4 opacity-30"><Document /></el-icon>
        <p class="font-medium text-sm">Select an order to view details</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrders, processOrder } from '@/api/admin'

const status = ref('All')
const dateRange = ref([])
const selected = ref(null)
const orders = ref([])

const pagination = reactive({ current: 1, size: 8, total: 0 })

const formatImageUrl = (url) => {
  if (!url) return ''
  return url.replace('10.0.2.2', 'localhost')
}

// 提取真实日期并加载
const fetchData = async () => {
  try {
    let sd = '', ed = ''
    if (dateRange.value && dateRange.value.length === 2) {
      // 提取东八区时间的日期字符串 YYYY-MM-DD
      sd = new Date(dateRange.value[0].getTime() - (dateRange.value[0].getTimezoneOffset() * 60000)).toISOString().split('T')[0]
      ed = new Date(dateRange.value[1].getTime() - (dateRange.value[1].getTimezoneOffset() * 60000)).toISOString().split('T')[0]
    }

    const res = await getAdminOrders({
      current: pagination.current,
      size: pagination.size,
      status: status.value,
      startDate: sd,
      endDate: ed
    })
    orders.value = res.records || []
    pagination.total = res.total || 0

    // 🔥 每次加载数据后，自动选中第一条（防抖：仅当当前没有选中或者选中的不在新列表里时）
    if (orders.value.length > 0) {
      const exists = orders.value.find(o => o.orderId === selected.value?.orderId)
      if (!exists) selected.value = orders.value[0]
    } else {
      selected.value = null
    }

  } catch (error) {
    console.error("Failed to load orders:", error)
  }
}

onMounted(() => {
  fetchData()
})

const handleStatusChange = (newStatus) => {
  status.value = newStatus
  pagination.current = 1
  fetchData()
}

const handleDateChange = () => {
  pagination.current = 1
  fetchData()
}

// 🔥 真实处理订单状态
const handleProcessOrder = async () => {
  if (!selected.value) return
  try {
    await processOrder(selected.value.orderId)
    ElMessage.success("Order status updated successfully!")
    // 记住当前选中的 ID，刷新后保持高亮
    const currentId = selected.value.orderId
    await fetchData()
    selected.value = orders.value.find(o => o.orderId === currentId) || orders.value[0]
  } catch (e) {}
}

// 🔥 纯前端快速导出当前数据的 CSV
const handleExport = () => {
  if (orders.value.length === 0) return ElMessage.warning("No data to export")
  
  let csvContent = "data:text/csv;charset=utf-8,Order ID,Amount,Type,Status,Date\n"
  orders.value.forEach(o => {
    let row = `${o.orderSn},${o.actualAmount},${o.orderType},${o.status},"${o.createdAt}"`
    csvContent += row + "\n"
  })
  
  const encodedUri = encodeURI(csvContent)
  const link = document.createElement("a")
  link.setAttribute("href", encodedUri)
  link.setAttribute("download", "Orders_Export.csv")
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success("Export downloaded successfully!")
}
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.bento-date-picker) {
  @apply bg-slate-50 border-none shadow-none rounded-xl !w-64;
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