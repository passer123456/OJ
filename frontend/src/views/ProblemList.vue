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

    <!-- 题目列表 -->
    <div class="problem-table">
      <el-table :data="problemList" stripe style="width: 100%">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { Search } from "@element-plus/icons-vue";
import axios from "@/views/utils/filterAxios.js";
import { reactive } from "vue";

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

const problemList = ref([]); // 添加问题列表的响应式引用

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

    // 正确的数据访问方式
    pagination.total = res.data.total; // 直接访问res.data.total
    pagination.currentPage = res.data.pageNum || 1;

    problemList.value = res.data.list || [];

    // 转换tags为数组
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
  pagination.currentPage = 1; // 重置到第一页
  load();
};

const handleCurrentChange = (newpage) => {
  pagination.currentPage = newpage;
  load();
};

const handleFilter = () => {
  console.log("筛选参数:", filterParams.value); // 打印筛选参数
  pagination.currentPage = 1; // 重置到第一页
  load(); // 统一使用load方法
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
</style>
