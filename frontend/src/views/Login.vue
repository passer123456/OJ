<template>
  <div class="auth-container">
    <div class="auth-box">
      <h2 class="auth-title">系统登录</h2>

      <el-form
        ref="loginForm"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="auth-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="auth-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="auth-footer">
          <span>还没有账号?</span>
          <el-link type="primary" @click="$router.push('/register')"
            >立即注册</el-link
          >
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import axios from "@/utils/filterAxios.js";

const router = useRouter();

const form = ref({
  username: "",
  password: "",
});

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 12, message: "长度在3到12个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 3, max: 18, message: "长度在3到18个字符", trigger: "blur" },
  ],
};

const loading = ref(false);
const loginForm = ref(null);

const handleLogin = async () => {
  try {
    await loginForm.value.validate();
    loading.value = true;

    const res = await axios.post("/user/login", form.value);
    console.log(res);

    // 存储token和用户信息
    // localStorage.setItem("token", res.data.token);
    localStorage.setItem("user", JSON.stringify(res.data || {}));

    ElMessage.success("登录成功");

    // 根据角色跳转
    if (res.data.role === "admin") {
      router.push("/admin/home");
    } else {
      router.push("/user/home");
    }
  } catch (error) {
    if (error?.errors) {
      // 表单验证错误（由 Element Plus 抛出）
      ElMessage.warning("请检查表单输入是否正确");
    } else {
      // 网络或 API 错误
      ElMessage.error(error.message || "登录失败");
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  background-image: url("https://source.unsplash.com/random/1600x900/?nature");
  background-size: cover;
  background-position: center;
}

.auth-box {
  width: 400px;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.auth-title {
  margin-bottom: 30px;
  text-align: center;
  color: #333;
}

.auth-form {
  margin-top: 20px;
}

.auth-btn {
  width: 100%;
  margin-top: 10px;
}

.auth-footer {
  margin-top: 20px;
  text-align: center;
  color: #666;
}

.auth-footer .el-link {
  margin-left: 5px;
}
</style>
