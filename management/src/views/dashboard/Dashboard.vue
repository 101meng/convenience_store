<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-2xl font-bold text-slate-800 tracking-tight">Dashboard Overview</h2>
      <p class="text-slate-500 text-sm mt-1">Welcome back to Bento Box Admin.</p>
    </div>

    <div class="grid grid-cols-4 gap-6">
      <div class="bg-white p-6 rounded-layout shadow-bento border border-slate-50">
        <div class="flex justify-between items-start">
          <p class="text-sm font-bold text-slate-800">Total Revenue</p>
          <div class="w-8 h-8 bg-indigo-50 rounded-lg flex items-center justify-center text-primary">
            <el-icon><Wallet /></el-icon>
          </div>
        </div>
        <div class="mt-4">
          <p class="text-3xl font-black text-slate-800">${{ stats.totalRevenue || '0.00' }}</p>
          <p :class="['text-xs font-semibold mt-2', stats.revenueGrowth >= 0 ? 'text-emerald-500' : 'text-rose-500']">
            {{ stats.revenueGrowth >= 0 ? '↗' : '↘' }} {{ stats.revenueGrowth > 0 ? '+' : '' }}{{ stats.revenueGrowth || 0 }}% vs yesterday
          </p>
        </div>
      </div>

      <div class="bg-white p-6 rounded-layout shadow-bento border border-slate-50">
        <div class="flex justify-between items-start">
          <p class="text-sm font-bold text-slate-800">Total Orders</p>
          <div class="w-8 h-8 bg-indigo-50 rounded-lg flex items-center justify-center text-primary">
            <el-icon><ShoppingCart /></el-icon>
          </div>
        </div>
        <div class="mt-4">
          <p class="text-3xl font-black text-slate-800">{{ stats.totalOrders || 0 }}</p>
          <p :class="['text-xs font-semibold mt-2', stats.ordersGrowth >= 0 ? 'text-emerald-500' : 'text-rose-500']">
            {{ stats.ordersGrowth >= 0 ? '↗' : '↘' }} {{ stats.ordersGrowth > 0 ? '+' : '' }}{{ stats.ordersGrowth || 0 }}% vs yesterday
          </p>
        </div>
      </div>

      <div class="bg-white p-6 rounded-layout shadow-bento border border-slate-50">
        <div class="flex justify-between items-start">
          <p class="text-sm font-bold text-slate-800">Registered Users</p>
          <div class="w-8 h-8 bg-indigo-50 rounded-lg flex items-center justify-center text-primary">
            <el-icon><User /></el-icon>
          </div>
        </div>
        <div class="mt-4">
          <p class="text-3xl font-black text-slate-800">{{ stats.newUsers || 0 }}</p>
          <p class="text-xs font-semibold text-slate-400 mt-2">Total accounts created</p>
        </div>
      </div>

      <div class="bg-white p-6 rounded-layout shadow-bento border border-slate-50">
        <div class="flex justify-between items-start">
          <p class="text-sm font-bold text-slate-800">Active Banners</p>
          <div class="w-8 h-8 bg-indigo-50 rounded-lg flex items-center justify-center text-primary">
            <el-icon><Picture /></el-icon>
          </div>
        </div>
        <div class="mt-4">
          <p class="text-3xl font-black text-slate-800">{{ stats.activeBanners || 0 }}</p>
          <p class="text-xs font-semibold text-slate-400 mt-2">Currently running</p>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-6">
      <div class="col-span-2 bg-white p-7 rounded-layout shadow-bento border border-slate-50 flex flex-col">
        <div class="flex justify-between items-center mb-6 shrink-0">
           <h3 class="text-lg font-bold text-slate-800">Revenue Trend</h3>
           <el-select v-model="timeRange" class="w-32 !border-none" @change="fetchCharts">
             <el-option label="Last 7 Days" :value="7" />
             <el-option label="Last 30 Days" :value="30" />
           </el-select>
        </div>
        <div ref="lineChartRef" class="w-full flex-1 min-h-[300px]"></div>
      </div>

      <div class="space-y-6 flex flex-col">
        <div class="bg-white p-7 rounded-layout shadow-bento border border-slate-50 flex-1 flex flex-col">
          <h3 class="text-lg font-bold text-slate-800 mb-2 shrink-0">Catalog Distribution</h3>
          <div ref="pieChartRef" class="w-full flex-1 min-h-[200px]"></div>
          <div class="mt-4 space-y-3 shrink-0">
            <div v-for="(item, index) in topCategories" :key="index" class="flex justify-between items-center text-sm">
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full" :style="{ backgroundColor: pieColors[index % pieColors.length] }"></div>
                <span class="font-semibold text-slate-700">{{ item.name }}</span>
              </div>
              <span class="font-bold text-slate-800">{{ item.value }} items</span>
            </div>
          </div>
        </div>

        <div class="bg-white p-7 rounded-layout shadow-bento border border-slate-50 shrink-0">
          <h3 class="text-lg font-bold text-slate-800 mb-4">Quick Actions</h3>
          <div class="space-y-3">
            <el-button type="primary" color="#4f46e5" class="w-full h-12 rounded-xl font-bold shadow-sm" icon="Plus" @click="$router.push('/products')">Manage Catalog</el-button>
            <el-button class="w-full h-12 rounded-xl font-bold border-slate-200 text-slate-700 hover:bg-slate-50" icon="List" @click="$router.push('/orders')">Process Orders</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { getAdminStats, getAdminCharts } from '@/api/admin'
import * as echarts from 'echarts'

const stats = ref({})
const timeRange = ref(7)
const topCategories = ref([])
const pieColors = ['#4f46e5', '#0d9488', '#eab308', '#f43f5e', '#8b5cf6']

const lineChartRef = ref(null)
const pieChartRef = ref(null)
let lineChart = null
let pieChart = null

const fetchCharts = async () => {
  try {
    const chartData = await getAdminCharts({ days: timeRange.value })
    if (chartData.salesDistribution) {
       topCategories.value = [...chartData.salesDistribution].sort((a, b) => b.value - a.value).slice(0, 2)
    }
    await nextTick()
    initLineChart(chartData.revenueTrend)
    initPieChart(chartData.salesDistribution)
  } catch (error) {}
}

const initLineChart = (trendData) => {
  if (!lineChartRef.value) return
  if (lineChart) lineChart.dispose()
  lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    grid: { left: '3%', right: '4%', bottom: '3%', top: '5%', containLabel: true },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category', boundaryGap: false, data: trendData.dates,
      axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#64748b' }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } } },
    series: [{
      name: 'Revenue', type: 'line', smooth: true, itemStyle: { color: '#4f46e5' },
      lineStyle: { width: 4 }, areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(79, 70, 229, 0.2)' }, { offset: 1, color: 'rgba(79, 70, 229, 0)' }])
      },
      data: trendData.revenues
    }]
  })
}

const initPieChart = (distributionData) => {
  if (!pieChartRef.value) return
  if (pieChart) pieChart.dispose()
  pieChart = echarts.init(pieChartRef.value)
  const total = distributionData.reduce((acc, curr) => acc + curr.value, 0)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    title: { text: `Total Items\n${total}`, left: 'center', top: 'center', textStyle: { color: '#64748b', fontSize: 12 } },
    series: [{
      type: 'pie', radius: ['65%', '85%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 4 },
      label: { show: false },
      data: distributionData.map((item, i) => ({ ...item, itemStyle: { color: pieColors[i % pieColors.length] } }))
    }]
  })
}

const handleResize = () => { lineChart?.resize(); pieChart?.resize() }

onMounted(async () => {
  try {
    stats.value = await getAdminStats()
    await fetchCharts()
    window.addEventListener('resize', handleResize)
  } catch (error) {}
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose(); pieChart?.dispose()
})
</script>

<style scoped>
@reference "../../assets/tailwind.css";
:deep(.el-select .el-input__wrapper) { @apply bg-slate-50 shadow-none font-semibold text-slate-600 rounded-lg; }
</style>