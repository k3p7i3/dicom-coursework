import axios from 'axios';

export let authHeader;

export const setAuthToken = (value) => {
    authHeader = value;
}

export default async function request({ method, url, body, params, headers }) {
  
  let authPair = authHeader ? { 'Authorization': `Basic ${authHeader}` } : { }

  const config = {
      url: url,
      method: method,
      headers: headers ? { ...authPair, ...headers } : authPair
  }

  if (body) {
      config.data = body;
  }

  if (params) {
      config.params = params;
  }

  return await axios(config);
}
