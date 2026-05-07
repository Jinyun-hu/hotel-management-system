<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6f7ff;">
              <el-icon color="#1890ff" :size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.todayCheckInCount || 0 }}</div>
              <div class="stat-label">今日入住</div>
              <div class="stat-trend" :class="statistics.todayCheckInRate >= 0 ? 'up' : 'down'">
                <el-icon v-if="statistics.todayCheckInRate >= 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ Math.abs(statistics.todayCheckInRate || 0) }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f6ffed;">
              <el-icon color="#52c41a" :size="24"><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalRooms || 0 }}</div>
              <div class="stat-label">总房间数</div>
              <div class="stat-desc">{{ statistics.floorCount || 0 }} 层楼</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #fff7e6;">
              <el-icon color="#fa8c16" :size="24"><Unlock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.availableRooms || 0 }}</div>
              <div class="stat-label">可用房间</div>
              <div class="stat-desc">入住率 {{ statistics.occupancyRate || 0 }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #fff1f0;">
              <el-icon color="#f5222d" :size="24"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.todayOrderCount || 0 }}</div>
              <div class="stat-label">今日订单</div>
              <div class="stat-trend" :class="statistics.orderGrowthRate >= 0 ? 'up' : 'down'">
                <el-icon v-if="statistics.orderGrowthRate >= 0"><ArrowUp /></el-icon>
                <el-icon v-else><ArrowDown /></el-icon>
                <span>{{ Math.abs(statistics.orderGrowthRate || 0) }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f9f0ff;">
              <el-icon color="#722ed1" :size="24"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatNumber(statistics.todayRevenue || 0) }}</div>
              <div class="stat-label">今日营收</div>
              <div class="stat-desc">累计 ¥{{ formatNumber(statistics.totalRevenue || 0) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8" :lg="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6fffb;">
              <el-icon color="#13c2c2" :size="24"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.occupancyRate || 0 }}%</div>
              <div class="stat-label">入住率</div>
              <div class="stat-desc">较昨日平稳</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>营收趋势</span>
              <el-radio-group v-model="dateRange" size="small" @change="handleDateRangeChange">
                <el-radio-button :value="7">近7天</el-radio-button>
                <el-radio-button :value="30">近30天</el-radio-button>
                <el-radio-button :value="90">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="revenueChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房间状态分布</span>
            </div>
          </template>
          <div ref="roomStatusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快捷操作 -->
    <el-card class="quick-actions" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <div class="action-list">
        <el-button type="primary" @click="$router.push('/orders')">
          <el-icon><Plus /></el-icon>
          新建订单
        </el-button>
        <el-button type="success" @click="$router.push('/rooms')">
          <el-icon><House /></el-icon>
          房间管理
        </el-button>
        <el-button type="warning" @click="$router.push('/room-types')">
          <el-icon><OfficeBuilding /></el-icon>
          房型管理
        </el-button>
        <el-button type="info" @click="exportData">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { User, House, Unlock, Document, Money, TrendCharts, ArrowUp, ArrowDown, Plus, OfficeBuilding, Download } from '@element-plus/icons-vue'
import { getStatistics, exportStatistics, getTrendData, getRoomStatusDistribution } from '@/api/statistics'

const statistics = ref({})
const dateRange = ref(7)
const revenueChartRef = ref(null)
const roomStatusChartRef = ref(null)
let revenueChart = null
let roomStatusChart = null
let refreshTimer = null

// 格式化数字
const formatNumber = (num) => {
  return num.toLocaleString('zh-CN')
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    console.log('获取统计数据，dateRange:', dateRange.value)
    const res = await getStatistics({ dateRange: dateRange.value })
    console.log('统计数据响应:', res)
    statistics.value = res.data || {}
    console.log('统计数据:', statistics.value)
    await fetchTrendData()
    await fetchRoomStatus()
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取趋势数据
const fetchTrendData = async () => {
  try {
    const res = await getTrendData({ dateRange: dateRange.value })
    initRevenueChart(res.data || {})
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

// 获取房间状态分布
const fetchRoomStatus = async () => {
  try {
    const res = await getRoomStatusDistribution()
    initRoomStatusChart(res.data || {})
  } catch (error) {
    console.error('获取房间状态分布失败:', error)
  }
}

// 初始化营收趋势图
const initRevenueChart = (data) => {
  if (revenueChartRef.value) {
    if (revenueChart) {
      revenueChart.dispose()
    }
    revenueChart = echarts.init(revenueChartRef.value)

    const revenueOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: data.dates || [],
        axisLine: { lineStyle: { color: '#ddd' } },
        axisLabel: { color: '#666' }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { lineStyle: { color: '#eee' } },
        axisLabel: { color: '#666' }
      },
      series: [{
        name: '营收',
        type: 'bar',
        barWidth: '40%',
        data: data.revenues || [],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3ac487' },
            { offset: 1, color: '#0b9c64' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }]
    }

    revenueChart.setOption(revenueOption)
  }
}

// 初始化房间状态分布图
const initRoomStatusChart = (data) => {
  if (roomStatusChartRef.value) {
    if (roomStatusChart) {
      roomStatusChart.dispose()
    }
    roomStatusChart = echarts.init(roomStatusChartRef.value)

    const roomStatusOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: '10%',
        top: 'center',
        data: ['已入住', '空闲', '清洁中', '维修中']
      },
      series: [{
        name: '房间状态',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: data.occupied || 0, name: '已入住', itemStyle: { color: '#52c41a' } },
          { value: data.available || 0, name: '空闲', itemStyle: { color: '#1890ff' } },
          { value: data.cleaning || 0, name: '清洁中', itemStyle: { color: '#faad14' } },
          { value: data.maintenance || 0, name: '维修中', itemStyle: { color: '#f5222d' } }
        ]
      }]
    }

    roomStatusChart.setOption(roomStatusOption)
  }
}

// 日期范围变化
const handleDateRangeChange = () => {
  fetchStatistics()
}

// 导出数据
const exportData = async () => {
  try {
    const response = await exportStatistics({ dateRange: dateRange.value })
    const blob = response.data
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `统计数据_${new Date().toISOString().split('T')[0]}.xlsx`
    link.click()
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 窗口大小变化时重新渲染图表
const handleResize = () => {
  revenueChart?.resize()
  roomStatusChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  window.addEventListener('resize', handleResize)
  
  // 添加定时刷新机制，每30秒刷新一次数据
  refreshTimer = setInterval(() => {
    fetchStatistics()
  }, 30000)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  revenueChart?.dispose()
  roomStatusChart?.dispose()
  
  // 清除定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .stat-cards {
    margin-bottom: 20px;
    
    .stat-card {
      margin-bottom: 20px;
      
      .stat-content {
        display: flex;
        align-items: center;
        gap: 15px;
        
        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        .stat-info {
          flex: 1;
          
          .stat-value {
            font-size: 24px;
            font-weight: bold;
            color: #2d3955;
            line-height: 1.2;
          }
          
          .stat-label {
            font-size: 14px;
            color: #909399;
            margin-top: 4px;
          }
          
          .stat-trend {
            font-size: 12px;
            margin-top: 4px;
            display: flex;
            align-items: center;
            gap: 2px;
            
            &.up {
              color: #52c41a;
            }
            
            &.down {
              color: #f5222d;
            }
          }
          
          .stat-desc {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }
  
  .chart-row {
    margin-bottom: 20px;
    
    .chart-card {
      margin-bottom: 20px;
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      
      .chart-container {
        height: 300px;
      }
    }
  }
  
  .quick-actions {
    .card-header {
      font-weight: bold;
    }
    
    .action-list {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
    }
  }
}
</style>
