import request from "./client"

export default class DicomService {

  static serviceUrl = 'http://localhost:8080/api/v1/dicom'

  static async uploadDicom({ filePath, file }) {
    const url = this.serviceUrl + '/upload';

    let body = new FormData();
    body.append('multipartFile', file)

    return await request({ 
      method: 'post', 
      url: url,
      body: body,
      params: { filePath: filePath },
      headers: { 'Content-type': 'multipart/form-data' }
    })
  }

  static async getDicomInfo({ filePath }) {
    const url = this.serviceUrl + '/get-info'

    return await request({ method: 'get', url: url, params: { filePath: filePath } })
  }

  static async getDicomFrame({ filePath, frame }) {
    const url = this.serviceUrl + '/get-frame'

    return await request({ 
      method: 'get',
      url: url,
      params: { filePath: filePath, frame: frame }
    })
  }

  static async deleteDicom({ filePath }) {
    const url = this.serviceUrl + '/delete'
    return await request({ 
      method: 'delete', 
      url: url, 
      body: filePath, 
      headers: {'Content-Type': 'text/plain'} 
    })
  }

  static async renameDicom({ oldFilePath, newFilePath }) {
    const url = this.serviceUrl + '/rename'

    return await request({ 
      method: 'post',
      url: url,
      body: { oldFilePath: oldFilePath, newFilePath: newFilePath },
      headers: {'Content-Type': 'application/json'}
    })
  }

  static async createDirectory({ dirPath }) {
    const url = this.serviceUrl + '/dir/create'
    return await request({ 
      method: 'post', 
      url: url, 
      body: dirPath, 
      headers: {'Content-Type': 'text/plain'} 
    })
  }

  static async deleteDirectory({ dirPath }) {
    const url = this.serviceUrl + '/dir/delete'
    return await request({ 
      method: 'delete', 
      url: url, 
      body: dirPath, 
      headers: {'Content-Type': 'text/plain'} 
    })
  }

  static async renameDirectory({ oldPath, newPath }) {
    const url = this.serviceUrl + '/dir/rename'

    return await request({ 
      method: 'post',
      url: url,
      body: { oldFilePath: oldPath, newFilePath: newPath },
      headers: {'Content-Type': 'application/json'}
    })
  }

  static async getDirectoryContents({ dir }) {
    const url = this.serviceUrl + '/dir/get-content'
    return await request({ method: 'get', url: url, params: { dirPath: dir }})
  }

  static async getPatients({ userEmail }) {
    const url = this.serviceUrl + '/patients'
    return await request({ method: 'get', url: url, params: { username: userEmail }})
  }

  static async getStudies({ patientUid }) {
    const url = this.serviceUrl + '/studies'
    return await request({ method: 'get', url: url, params: { patientUid: patientUid }})
  }

  static async getSeries({ studyUid }) {
    const url = this.serviceUrl + '/series'
    return await request({ method: 'get', url: url, params: { studyUid: studyUid }})
  }

  static async getDicoms({ seriesUid }) {
    const url = this.serviceUrl + '/dicom-list'
    return await request({ method: 'get', url: url, params: { seriesUid: seriesUid }})
  }

  static async downloadDicom({ filePath }) {
    const url = this.serviceUrl + '/download'
    return await request({ method: 'get', url: url, params: { filePath: filePath }})
  }
}