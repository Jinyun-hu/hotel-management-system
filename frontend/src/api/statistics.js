import request from './request'

// 获取统计数据
export const getStatistics = (params) => {
  return request.get('/statistics', { params })
}

// 获取趋势数据
export const getTrendData = (params) => {
  return request.get('/statistics/trend', { params })
}

// 获取房间状态分布
export const getRoomStatusDistribution = () => {
  return request.get('/statistics/room-status')
}

// 导出统计数据
export const exportStatistics = (params) => {
  return request.get('/statistics/export', {
    params,
    responseType: 'blob'
  })
}
