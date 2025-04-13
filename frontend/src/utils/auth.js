// 模拟用户数据
const users = [
  { id: 1, username: "admin", password: "admin123", role: "admin" },
  { id: 2, username: "user1", password: "user123", role: "user" },
];

// 模拟API请求
const fakeApiRequest = (success = true, data, delay = 1000) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (success) {
        resolve(data);
      } else {
        reject(new Error("Request failed"));
      }
    }, delay);
  });
};

// 注册
export const register = async (userData) => {
  // 检查用户名是否已存在
  if (users.some((user) => user.username === userData.username)) {
    return fakeApiRequest(false, { message: "用户名已存在" });
  }

  // 创建新用户
  const newUser = {
    id: users.length + 1,
    username: userData.username,
    password: userData.password,
    role: "user", // 默认角色
  };

  users.push(newUser);
  return fakeApiRequest(true, { message: "注册成功", user: newUser });
};

// 登录
export const login = async (credentials) => {
  const user = users.find(
    (u) =>
      u.username === credentials.username && u.password === credentials.password
  );

  if (!user) {
    return fakeApiRequest(false, { message: "用户名或密码错误" });
  }

  // 模拟返回token
  return fakeApiRequest(true, {
    token: "fake-jwt-token",
    user,
  });
};

// 检查认证状态
export const checkAuth = () => {
  return Promise.resolve(!!localStorage.getItem("token"));
};

// 登出
export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  return Promise.resolve();
};
