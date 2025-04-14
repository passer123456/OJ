import axios from "axios";
import { ElMessage } from "element-plus";

const instance = axios.create({
  baseURL: "http://localhost:8082/api",
  timeout: 10000,
  withCredentials: true,
});
instance.interceptors.request.use(
  (config) => {
    config.headers["Content-Type"] = "application/json;charset=UTF-8";
    let user = JSON.parse(localStorage.getItem("user") || "{}");
    if (user.token) {
      config.headers["token"] = user.token; // 只有存在 token 时才添加
    }
    return config;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  }
);
instance.interceptors.response.use(
  (response) => {
    let res = response.data;
    if (typeof res === "string") {
      res = res ? JSON.parse(res) : res;
    }
    return res;
  },
  (error) => {
    if (error.response.status === 404) {
      ElMessage.error(" 未找到接口");
    } else if (error.response.status === 500) {
      ElMessage.error("系统异常");
    } else if (error.response.status === 401) {
      ElMessage.error(error.msg);
      // localStorage.removeItem("user");
      // window.location.href = "/login";
    } else {
      console.log(error.message);
    }
    return Promise.reject(error);
  }
);
export default instance;
