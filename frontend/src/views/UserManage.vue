<template>
  <div class="user-list-container">
    <!-- 顶部筛选栏 -->
    <div class="filter-bar">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="filterParams.keyword"
            placeholder="搜索用户名"
            clearable
            @clear="handleFilter"
            @keyup.enter="handleFilter"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="filterParams.role"
            placeholder="用户角色"
            clearable
          >
            <el-option label="全部" value="" />
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleFilter">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons" style="margin-bottom: 20px">
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon> 增加用户
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedUsers.length === 0"
        @click="handleDeleteBatch"
      >
        <el-icon><Delete /></el-icon> 批量删除
      </el-button>
      <el-button type="warning" :disabled="!selectedUser" @click="handleEdit">
        <el-icon><Edit /></el-icon> 修改用户
      </el-button>
    </div>

    <!-- 用户列表 -->
    <div class="user-table">
      <el-table
        :data="userList"
        stripe
        style="width: 100%"
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="password" label="密码" width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : ''">
              {{ row.role === "admin" ? "管理员" : "普通用户" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="100">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar || defaultAvatar" />
          </template>
        </el-table-column>
        <el-table-column label="个人简介">
          <template #default="{ row }">
            <el-tooltip effect="light" placement="top" v-if="row.bio">
              <template #content>
                <div style="max-width: 300px">{{ row.bio }}</div>
              </template>
              <span class="bio-preview">
                {{
                  row.bio
                    ? row.bio.substring(0, 15) +
                      (row.bio.length > 15 ? "..." : "")
                    : "无"
                }}
              </span>
            </el-tooltip>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click.stop="handleEditUser(row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click.stop="handleDeleteUser(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      :destroy-on-close="true"
      width="50%"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="userForm.rules"
        label-width="100px"
      >
        <el-form-item prop="username" label="用户名">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password" :label="isEditMode ? '新密码' : '密码'">
          <el-input
            v-model="userForm.password"
            type="password"
            :placeholder="isEditMode ? '留空则不修改密码' : '请输入密码'"
          ></el-input>
        </el-form-item>
        <el-form-item prop="role" label="用户角色">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="普通用户" value="user"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input
            v-model="userForm.avatar"
            placeholder="请输入头像URL"
          ></el-input>
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="userForm.bio"
            type="textarea"
            :rows="3"
            placeholder="请输入个人简介"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import { Search, Plus, Delete, Edit } from "@element-plus/icons-vue";
import axios from "@/utils/filterAxios.js";
import { ElMessage, ElMessageBox } from "element-plus";

// 默认头像
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

// 筛选参数
const filterParams = ref({
  keyword: "",
  role: "",
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

const userList = ref([]);
const selectedUser = ref(null);
const selectedUsers = ref([]);
const dialogVisible = ref(false);
const isEditMode = ref(false);
const dialogTitle = ref("添加用户");
const formRef = ref(null);

// 用户表单
const userForm = reactive({
  userId: "",
  username: "",
  password: "",
  role: "user",
  avatar: "",
  bio: "",
  rules: {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    password: [
      {
        required: true,
        message: "请输入密码",
        trigger: "blur",
        validator: (rule, value, callback) => {
          if (!isEditMode.value && !value) {
            callback(new Error("请输入密码"));
          } else {
            callback();
          }
        },
      },
    ],
    role: [{ required: true, message: "请选择用户角色", trigger: "change" }],
  },
  rows: [],
});

const load = async () => {
  try {
    const res = await axios.get("/user/selectPage", {
      params: {
        keyword: filterParams.value.keyword,
        role: filterParams.value.role,
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
      },
    });

    pagination.total = res.data.total;
    pagination.currentPage = res.data.pageNum || 1;
    userList.value = res.data.list || [];
  } catch (error) {
    console.error("加载用户列表失败:", error);
    ElMessage.error("加载用户列表失败");
  }
};

// 分页变更处理
const handleSizeChange = (newsize) => {
  pagination.pageSize = newsize;
  pagination.currentPage = 1;
  load();
};

const handleCurrentChange = (newpage) => {
  pagination.currentPage = newpage;
  load();
};

const handleFilter = () => {
  pagination.currentPage = 1;
  load();
};

// 重置筛选
const resetFilter = () => {
  filterParams.value = {
    keyword: "",
    role: "",
  };
  handleFilter();
};

// 选中行事件
const handleSelectionChange = (rows) => {
  selectedUsers.value = rows;
  console.log("选中的用户:", selectedUsers.value);
};

// 添加用户
const handleAdd = () => {
  isEditMode.value = false;
  dialogTitle.value = "添加用户";
  Object.assign(userForm, {
    userId: "",
    username: "",
    password: "",
    role: "user",
    avatar: "",
    bio: "",
  });
  dialogVisible.value = true;
};

// 编辑用户
const handleEdit = () => {
  if (!selectedUser.value) return;

  isEditMode.value = true;
  dialogTitle.value = "编辑用户";
  Object.assign(userForm, {
    userId: selectedUser.value.userId,
    username: selectedUser.value.username,
    password: "",
    role: selectedUser.value.role,
    avatar: selectedUser.value.avatar || "",
    bio: selectedUser.value.bio || "",
  });
  dialogVisible.value = true;
};

// 快捷编辑
const handleEditUser = (row) => {
  selectedUser.value = row;
  handleEdit();
};

// 删除用户
const handleDelete = async () => {
  if (!selectedUser.value) return;

  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${selectedUser.value.username}" 吗?`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await axios.delete(`/user/delete/${selectedUser.value.userId}`);
    ElMessage.success("删除成功");
    load();
    selectedUser.value = null;
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 快捷删除
const handleDeleteUser = (row) => {
  selectedUser.value = row;
  handleDelete();
};

// 批量删除
// 删除用户
const handleDeleteBatch = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning("请至少选择一条数据");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await axios.delete(`/user/deleteBatch`, {
      data: selectedUsers.value.map((user) => user.userId),
    });

    ElMessage.success("批量删除成功");
    load();
    selectedUsers.value = []; // 清空选中
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

// 头像上传成功
const handleAvatarSuccess = (response) => {
  userForm.avatar = response.data.url;
  ElMessage.success("头像上传成功");
};

const submitUser = async () => {
  try {
    await formRef.value.validate();

    const data = { ...userForm };
    // 如果是编辑模式且密码为空，则不更新密码
    if (isEditMode.value && !data.password) {
      delete data.password;
    }

    const res = isEditMode.value
      ? await axios.put("/user/update", data)
      : await axios.post("/user/add", data);

    if (res.code !== 200) throw new Error(res.msg);

    ElMessage.success(isEditMode.value ? "更新成功" : "添加成功");
    dialogVisible.value = false;
    load();
  } catch (error) {
    ElMessage.error(error.message || "操作失败");
    console.error(error);
  }
};

// 初始化加载
onMounted(load);
</script>

<style scoped>
.user-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filter-bar {
  margin-bottom: 20px;
}

.user-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.bio-preview {
  cursor: pointer;
  color: var(--el-color-primary);
}

.avatar-uploader {
  margin-top: 10px;
}
</style>
