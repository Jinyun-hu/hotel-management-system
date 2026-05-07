import request from './request'

// 查询房型列表
export const getRoomTypeList = (params) => {
  return request.get('/room-types', { params })
}

// 新增房型
export const addRoomType = (data) => {
  return request.post('/room-types', data)
}

// 编辑房型
export const updateRoomType = (id, data) => {
  return request.put(`/room-types/${id}`, data)
}

// 删除房型
export const deleteRoomType = (id) => {
  return request.delete(`/room-types/${id}`)
}

// 检查房型关联的订单
export const checkRoomTypeOrders = (id) => {
  return request.get(`/room-types/${id}/orders`)
}

// 更新房型状态
export const updateRoomTypeStatus = (id, status) => {
  return request.patch(`/room-types/${id}/status`, { status })
}

// 上传图片
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
