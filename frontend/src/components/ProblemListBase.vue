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
            @change="handleFilter"
          >
            <el-option label="全部" value="" />
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="filterParams.tag"
            placeholder="标签"
            clearable
            @change="handleFilter"
          >
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
      <el-table
        :data="problemList"
        stripe
        style="width: 100%"
        @row-click="emitRowClick"
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
              {{ difficultyMap[row.difficulty] || row.difficulty }}
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
import { ref, reactive, defineProps, defineEmits } from "vue";
import { Search } from "@element-plus/icons-vue";

const props = defineProps({
  tagOptions: {
    type: Array,
    default: () => [
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
    ],
  },
});

const emit = defineEmits(["filter-change", "row-click"]);

// 难度映射
const difficultyMap = {
  easy: "简单",
  medium: "中等",
  hard: "困难",
};

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
});

const problemList = ref([]);
const selectedProblem = ref(null);

// 行点击事件
const emitRowClick = (row) => {
  selectedProblem.value = row;
  emit("row-click", row);
};

// 分页变更处理
const handleSizeChange = (newSize) => {
  pagination.pageSize = newSize;
  pagination.currentPage = 1;
  emitFilterChange();
};

const handleCurrentChange = (newPage) => {
  pagination.currentPage = newPage;
  emitFilterChange();
};

// 筛选处理
const handleFilter = () => {
  pagination.currentPage = 1;
  emitFilterChange();
};

// 触发筛选变化事件
const emitFilterChange = () => {
  emit("filter-change", {
    ...filterParams.value,
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize,
  });
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

// 设置问题列表数据
const setProblemList = (list, total) => {
  problemList.value = list;
  pagination.total = total || list.length;
};

// 暴露方法和状态供父组件使用
defineExpose({
  problemList,
  selectedProblem,
  pagination,
  filterParams,
  setProblemList,
  resetFilter,
});
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
