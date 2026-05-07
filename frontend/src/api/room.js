import request from './request'

// 查询房间列表
export const getRoomList = (params) => {
  return request.get('/rooms', { params })
}

// 新增房间
export const addRoom = (data) => {
  return request.post('/rooms', data)
}

// 编辑房间
export const updateRoom = (id, data) => {
  return request.put(`/rooms/${id}`, data)
}

// 删除房间
export const deleteRoom = (id) => {
  return request.delete(`/rooms/${id}`)
}

// 检查房间关联的订单
export const checkRoomOrders = (id) => {
  return request.get(`/rooms/${id}/orders`)
}
