import request from './request'

// 查询订单列表
export const getOrderList = (params) => {
  return request.get('/orders', { params })
}

// 新增订单
export const addOrder = (data) => {
  return request.post('/orders', data)
}

// 编辑订单
export const updateOrder = (id, data) => {
  return request.put(`/orders/${id}`, data)
}

// 删除订单
export const deleteOrder = (id) => {
  return request.delete(`/orders/${id}`)
}

// 获取订单详情
export const getOrderDetail = (id) => {
  return request.get(`/orders/${id}`)
}

// 取消订单
export const cancelOrder = (id) => {
  return request.post(`/orders/${id}/cancel`)
}

// 更新订单支付状态
export const updateOrderPaymentStatus = (id, paymentStatus) => {
  return request.patch(`/orders/${id}/payment-status`, { paymentStatus })
}

// 检查过期订单
export const checkExpiredOrders = () => {
  return request.post('/orders/check-expired')
}
