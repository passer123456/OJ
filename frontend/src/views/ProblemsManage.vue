<template>
  <div class="problem-list-container">
    <!-- 顶部筛选栏 -->
    <div class="filter-bar">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="filterParams.keyword"
            placeholder="搜索题目"
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
            v-model="filterParams.difficulty"
            placeholder="难度"
            clearable
          >
            <el-option label="全部" value="" />
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterParams.tag" placeholder="标签" clearable>
            <el-option label="全部" value="" />
            <el-option
              v-for="tag in tagOptions"
              :key="tag"
              :label="tag"
              :value="tag"
            />
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
        <el-icon><Plus /></el-icon> 增加
      </el-button>
      <el-button
        type="danger"
        :disabled="!selectedProblem"
        @click="handleDelete"
      >
        <el-icon><Delete /></el-icon> 删除
      </el-button>
      <el-button
        type="warning"
        :disabled="!selectedProblem"
        @click="handleEdit"
      >
        <el-icon><Edit /></el-icon> 修改
      </el-button>
    </div>

    <!-- 题目列表 -->
    <div class="problem-table">
      <el-table
        :data="problemList"
        stripe
        style="width: 100%"
        @row-click="handleRowClick"
        highlight-current-row
      >
        <el-table-column prop="problemid" label="题号" width="100" />
        <el-table-column prop="title" label="标题" width="200">
          <template #default="{ row }">
            <router-link
              :to="`/problem/${row.problemid}`"
              class="problem-title"
            >
              {{ row.title }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="120">
          <template #default="{ row }">
            <el-tag
              :type="
                row.difficulty === 'easy'
                  ? 'success'
                  : row.difficulty === 'medium'
                  ? 'warning'
                  : 'danger'
              "
            >
              {{
                row.difficulty === "easy"
                  ? "简单"
                  : row.difficulty === "medium"
                  ? "中等"
                  : "困难"
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签">
          <template #default="{ row }">
            <el-space wrap>
              <el-tag
                v-for="tag in row.tags"
                :key="tag"
                size="small"
                type="info"
              >
                {{ tag }}
              </el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column prop="acRate" label="通过率" width="120">
          <template #default="{ row }"> {{ row.acRate }}% </template>
        </el-table-column>
        <el-table-column prop="submitCount" label="提交数" width="120" />
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
      width="70%"
    >
      <el-form
        ref="formRef"
        :model="problemForm"
        :rules="problemForm.rules"
        label-width="100px"
      >
        <el-form-item prop="title" label="题目标题">
          <el-input
            v-model="problemForm.title"
            placeholder="请输入标题"
          ></el-input>
        </el-form-item>
        <el-form-item prop="difficulty" label="题目难度">
          <el-select v-model="problemForm.difficulty" placeholder="请选择难度">
            <el-option label="简单" value="easy"></el-option>
            <el-option label="中等" value="medium"></el-option>
            <el-option label="困难" value="hard"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目标签">
          <el-select
            v-model="problemForm.tags"
            multiple
            filterable
            allow-create
            placeholder="请选择或输入标签"
          >
            <el-option
              v-for="tag in tagOptions"
              :key="tag"
              :label="tag"
              :value="tag"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="description" label="题目描述">
          <el-input
            v-model="problemForm.description"
            type="textarea"
            :rows="5"
            placeholder="请输入题目描述"
          ></el-input>
        </el-form-item>
        <el-form-item prop="templateCode" label="模板代码">
          <el-input
            v-model="problemForm.templateCode"
            type="textarea"
            :rows="5"
            placeholder="请输入模板代码"
          ></el-input>
        </el-form-item>
        <el-form-item prop="test_code" label="测试代码">
          <el-input
            v-model="problemForm.test_code"
            type="textarea"
            :rows="5"
            placeholder="请输入测试代码"
          ></el-input>
        </el-form-item>
        <el-form-item prop="sample_input" label="样例输入">
          <el-input
            v-model="problemForm.sample_input"
            type="textarea"
            :rows="3"
            placeholder="请输入样例输入"
          ></el-input>
        </el-form-item>
        <el-form-item prop="sample_output" label="样例输出">
          <el-input
            v-model="problemForm.sample_output"
            type="textarea"
            :rows="3"
            placeholder="请输入样例输出"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProblem">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Search, Plus, Delete, Edit } from "@element-plus/icons-vue";
import axios from "@/utils/filterAxios.js";
import { reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
// import { te } from "element-plus/es/locale";

// 筛选参数
const filterParams = ref({
  keyword: "",
  difficulty: "",
  tag: "",
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
  data: [],
});

const problemList = ref([]);
const selectedProblem = ref(null); // 当前选中的题目
const dialogVisible = ref(false); // 对话框显示状态
const isEditMode = ref(false); // 是否为编辑模式
const dialogTitle = ref("添加题目"); // 对话框标题
const formRef = ref(null); // 表单引用

// 题目表单
const problemForm = reactive({
  problemid: "",
  title: "",
  difficulty: "",
  tags: [],
  description: "",
  templateCode: "",
  testCode: "",
  sampleInput: "",
  sampleOutput: "",
  submitCount: 0,
  acceptCount: 0,
  rules: {
    title: [{ required: true, message: "请输入题目标题", trigger: "blur" }],
    difficulty: [
      { required: true, message: "请选择题目难度", trigger: "change" },
    ],
    description: [
      { required: true, message: "请输入题目描述", trigger: "blur" },
    ],
    test_code: [
      { required: false, message: "请输入测试代码", trigger: "blur" },
    ],
    sample_input: [
      { required: false, message: "请输入样例输入", trigger: "blur" },
    ],
    sample_output: [
      { required: false, message: "请输入样例输出", trigger: "blur" },
    ],
  },
});

// 标签选项
const tagOptions = ref([
  "数组",
  "字符串",
  "哈希表",
  "动态规划",
  "数学",
  "排序",
  "贪心",
  "树",
  "图",
  "二分查找",
  "广度优先搜索",
  "深度优先搜索",
  "回溯",
  "栈",
  "队列",
]);

const load = async () => {
  try {
    const res = await axios.get("/problem/selectPage", {
      params: {
        keyword: filterParams.value.keyword,
        difficulty: filterParams.value.difficulty,
        tag: filterParams.value.tag,
        pageNum: pagination.currentPage,
        pageSize: pagination.pageSize,
      },
    });

    pagination.total = res.data.total;
    pagination.currentPage = res.data.pageNum || 1;

    problemList.value = res.data.list || [];

    problemList.value = problemList.value.map((item) => ({
      ...item,
      tags: item.tags ? item.tags.split(/[,，]/) : [],
      acRate:
        item.submitCount > 0
          ? Math.round((item.acceptCount / item.submitCount) * 100)
          : 0,
    }));
  } catch (error) {
    console.error("加载问题列表失败:", error);
    ElMessage.error("加载问题列表失败");
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
    difficulty: "",
    tag: "",
  };
  handleFilter();
};

// 行点击事件
const handleRowClick = (row) => {
  selectedProblem.value = row;
};

// 添加题目
const handleAdd = () => {
  isEditMode.value = false;
  dialogTitle.value = "添加题目";
  // 重置表单
  Object.assign(problemForm, {
    problemid: "",
    title: "",
    difficulty: "",
    tags: [],
    description: "",
    templateCode: "",
    testCode: "",
    sampleInput: "",
    sampleOutput: "",
    submitCount: 0,
    acceptCount: 0,
  });
  dialogVisible.value = true;
};

// 编辑题目
const handleEdit = () => {
  if (!selectedProblem.value) return;
  console.log(selectedProblem.value);

  isEditMode.value = true;
  dialogTitle.value = "编辑题目";
  // 填充表单
  Object.assign(problemForm, {
    problemid: selectedProblem.value.problemid,
    title: selectedProblem.value.title,
    difficulty: selectedProblem.value.difficulty,
    tags: [...selectedProblem.value.tags],
    description: selectedProblem.value.description || "",
    templateCode: selectedProblem.value.templateCode || "",
    testCode: selectedProblem.value.testCode || "",
    sampleInput: selectedProblem.value.sampleInput || "",
    sampleOutput: selectedProblem.value.sampleOutput || "",
    submitCount: selectedProblem.value.submitCount || 0,
    acceptCount: selectedProblem.value.acceptCount || 0,
  });
  dialogVisible.value = true;
};

// 删除题目
const handleDelete = async () => {
  if (!selectedProblem.value) return;

  try {
    await ElMessageBox.confirm(
      `确定要删除题目 "${selectedProblem.value.title}" 吗?`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await axios.delete(`/problem/delete/${selectedProblem.value.problemid}`);
    ElMessage.success("删除成功");
    load(); // 刷新列表
    selectedProblem.value = null; // 清空选中
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const submitProblem = async () => {
  try {
    if (!formRef.value) return;

    await formRef.value.validate();

    const { rules, ...rest } = problemForm; // 解构，排除 rules
    const data = {
      ...rest, // 展开剩余属性
      tags: problemForm.tags.join(","), // 单独处理 tags
    };

    const res = isEditMode.value
      ? await axios.put("/problem/update", data)
      : await axios.post("/problem/add", data);

    console.log(data);

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
.problem-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filter-bar {
  margin-bottom: 20px;
}

.problem-table {
  margin-bottom: 20px;
}

.problem-title {
  color: var(--el-color-primary);
  text-decoration: none;
}

.problem-title:hover {
  text-decoration: underline;
}

.pagination {
  display: flex;
  justify-content: center;
}

.action-buttons {
  display: flex;
  gap: 10px;
}
</style>
