<template>
  <div class="room-status-container">
    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="filterForm" inline>
        <el-form-item label="楼层">
          <el-select v-model="filterForm.floor" placeholder="全部楼层" clearable>
            <el-option
              v-for="floor in floorOptions"
              :key="floor"
              :label="`${floor}楼`"
              :value="floor"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="filterForm.roomTypeId" placeholder="全部房型" clearable>
            <el-option
              v-for="item in roomTypeOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="已入住" value="occupied" />
            <el-option label="空闲" value="available" />
            <el-option label="等待清洁" value="waiting_clean" />
            <el-option label="清洁中" value="cleaning" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">
            <el-icon><Search /></el-icon>
            筛选
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 图例说明 -->
    <el-card class="legend-card" shadow="never">
      <div class="legend-list">
        <div class="legend-item">
          <div class="legend-color occupied"></div>
          <span>已入住</span>
        </div>
        <div class="legend-item">
          <div class="legend-color available"></div>
          <span>空闲</span>
        </div>
        <div class="legend-item">
          <div class="legend-color waiting_clean"></div>
          <span>等待清洁</span>
        </div>
        <div class="legend-item">
          <div class="legend-color cleaning"></div>
          <span>清洁中</span>
        </div>
        <div class="legend-item">
          <div class="legend-color maintenance"></div>
          <span>维修中</span>
        </div>
      </div>
    </el-card>
    
    <!-- 房态展示 -->
    <el-card class="room-status-card" shadow="never" v-loading="loading">
      <div v-for="floor in groupedRooms" :key="floor.floor" class="floor-section">
        <div class="floor-title">{{ floor.floor }}楼</div>
        <div class="room-grid">
          <div
            v-for="room in floor.rooms"
            :key="room.id"
            class="room-item"
            :class="room.status"
            @click="handleRoomClick(room)"
          >
            <div class="room-number">{{ room.roomNumber }}</div>
            <div class="room-type">{{ room.roomTypeName }}</div>
            <div class="room-status">{{ getStatusLabel(room.status) }}</div>
            <div class="room-price">¥{{ room.price }}</div>
          </div>
        </div>
      </div>
      
      <el-empty v-if="groupedRooms.length === 0" description="暂无房间数据" />
    </el-card>
    
    <!-- 房间详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="房间详情"
      width="400px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="房间编号">{{ currentRoom?.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="房型">{{ currentRoom?.roomTypeName }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ currentRoom?.floor }}楼</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRoom?.status)">
            {{ getStatusLabel(currentRoom?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentRoom?.price }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleEditRoom">编辑房间</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getRoomList } from '@/api/room'
import { getRoomTypeList } from '@/api/roomType'

const router = useRouter()
const loading = ref(false)
const roomList = ref([])
const roomTypeOptions = ref([])
const floorOptions = ref([1, 2, 3, 4, 5, 6, 7, 8])

const filterForm = reactive({
  floor: '',
  roomTypeId: '',
  status: ''
})

const detailVisible = ref(false)
const currentRoom = ref(null)

// 状态映射
const statusMap = {
  occupied: { label: '已入住', type: 'success' },
  available: { label: '空闲', type: 'info' },
  waiting_clean: { label: '等待清洁', type: 'warning' },
  cleaning: { label: '清洁中', type: 'warning' },
  maintenance: { label: '维修中', type: 'danger' }
}

const getStatusLabel = (status) => statusMap[status]?.label || status
const getStatusType = (status) => statusMap[status]?.type || ''

// 按楼层分组
const groupedRooms = computed(() => {
  const groups = {}
  
  let filtered = roomList.value
  
  if (filterForm.floor) {
    filtered = filtered.filter(r => r.floor === filterForm.floor)
  }
  if (filterForm.roomTypeId) {
    filtered = filtered.filter(r => r.roomTypeId === filterForm.roomTypeId)
  }
  if (filterForm.status) {
    filtered = filtered.filter(r => r.status === filterForm.status)
  }
  
  filtered.forEach(room => {
    if (!groups[room.floor]) {
      groups[room.floor] = []
    }
    groups[room.floor].push(room)
  })
  
  return Object.keys(groups)
    .sort((a, b) => b - a)
    .map(floor => ({
      floor: parseInt(floor),
      rooms: groups[floor].sort((a, b) => a.roomNumber.localeCompare(b.roomNumber))
    }))
})

// 获取房间列表
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoomList({ page: 1, size: 1000 })
    const pageData = res.data || {}
    roomList.value = pageData.data || []
  } catch (error) {
    console.error('获取房间列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取房型列表
const fetchRoomTypes = async () => {
  try {
    const res = await getRoomTypeList({ page: 1, size: 1000 })
    const pageData = res.data || {}
    roomTypeOptions.value = pageData.data || []
  } catch (error) {
    console.error('获取房型列表失败:', error)
  }
}

// 筛选
const handleFilter = () => {
  // 筛选逻辑在computed中处理
}

// 重置
const handleReset = () => {
  filterForm.floor = ''
  filterForm.roomTypeId = ''
  filterForm.status = ''
}

// 点击房间
const handleRoomClick = (room) => {
  currentRoom.value = room
  detailVisible.value = true
}

// 编辑房间
const handleEditRoom = () => {
  detailVisible.value = false
  router.push('/rooms')
}

onMounted(() => {
  fetchData()
  fetchRoomTypes()
})
</script>

<style scoped lang="scss">
.room-status-container {
  .filter-card {
    margin-bottom: 20px;
  }
  
  .legend-card {
    margin-bottom: 20px;
    
    .legend-list {
      display: flex;
      gap: 30px;
      
      .legend-item {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .legend-color {
          width: 20px;
          height: 20px;
          border-radius: 4px;
          
          &.occupied {
            background-color: #52c41a;
          }
          
          &.available {
            background-color: #1890ff;
          }
          
          &.waiting_clean {
            background-color: #722ed1;
          }
          
          &.cleaning {
            background-color: #faad14;
          }
          
          &.maintenance {
            background-color: #f5222d;
          }
        }
      }
    }
  }
  
  .room-status-card {
    .floor-section {
      margin-bottom: 30px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .floor-title {
        font-size: 18px;
        font-weight: bold;
        color: #2d3955;
        margin-bottom: 15px;
        padding-left: 10px;
        border-left: 4px solid #3ac487;
      }
      
      .room-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 15px;
        
        .room-item {
          padding: 15px;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s;
          text-align: center;
          color: #fff;
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          }
          
          &.occupied {
            background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
          }
          
          &.available {
            background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
          }
          
          &.waiting_clean {
            background: linear-gradient(135deg, #722ed1 0%, #531dab 100%);
          }
          
          &.cleaning {
            background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
          }
          
          &.maintenance {
            background: linear-gradient(135deg, #f5222d 0%, #cf1322 100%);
          }
          
          .room-number {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
          }
          
          .room-type {
            font-size: 12px;
            opacity: 0.9;
            margin-bottom: 3px;
          }
          
          .room-status {
            font-size: 12px;
            opacity: 0.8;
            margin-bottom: 5px;
          }
          
          .room-price {
            font-size: 14px;
            font-weight: bold;
          }
        }
      }
    }
  }
}
</style>
