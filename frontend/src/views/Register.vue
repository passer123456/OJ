<template>
  <div class="auth-container">
    <div class="auth-box">
      <h2 class="auth-title">用户注册</h2>

      <el-form
        ref="registerForm"
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

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="auth-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="auth-footer">
          <span>已有账号?</span>
          <el-link type="primary" @click="$router.push('/login')"
            >立即登录</el-link
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
  confirmPassword: "",
});

const validatePassword = (rule, value, callback) => {
  if (value !== form.value.password) {
    callback(new Error("两次输入密码不一致"));
  } else {
    callback();
  }
};

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 12, message: "长度在3到12个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 3, max: 18, message: "长度在3到18个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    { validator: validatePassword, trigger: "blur" },
  ],
};

const loading = ref(false);
const registerForm = ref(null);

const handleRegister = async () => {
  try {
    await registerForm.value.validate();
    loading.value = true;

    const res = await axios.post("/user/register", {
      username: form.value.username,
      password: form.value.password,
      role: "user",
    });
    console.log(res);
    if (res.code === 200) {
      ElMessage.success(res.msg || "注册成功，请登录");
      router.push("/login");
    } else if (res.code === 400) {
      ElMessage.error(res.msg || "注册失败，请重试");
    }
  } catch (error) {
    ElMessage.error(error.msg || "注册失败");
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
