import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080', // Base URL for your backend
  withCredentials: true, // Include cookies in every request
});

export default api;
