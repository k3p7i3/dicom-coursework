import request from "./client"


export default class UserService {

  static serviceUrl = 'http://localhost:8080/api/v1/user'

  static async getUser({ email }) {
    const url = this.serviceUrl + '/get'
    return await request({ method: 'get', url: url, params: { email: email }})
  }

  static async createUser({ userData }) {
    const url = this.serviceUrl + '/create'

    return await request({
      method: 'post',
      url: url,
      body: { ...userData }
    })
  }

  static async editUser({ email, userNewData }) {
    const url = this.serviceUrl + '/edit'

    return await request({
      method: 'post',
      url: url,
      body: {
        userEmail: email,
        edit: { ...userNewData }
      }
    })
  }

  static async changePassword({ userData }) {
    const url = this.serviceUrl + '/change-password'

    return await request({
      method: 'post',
      url: url,
      body: {...userData},
      headers: {'Content-Type': 'application/json'}
    })
  }
}