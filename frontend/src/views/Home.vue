<template>
  <div>Home</div>
</template>

<script setup>
import axios from "@/utils/filterAxios.js";

axios
  .get("/user/selectAll")
  .then((res) => {
    console.log(res.data);
  })
  .catch((err) => {
    console.log(err);
  });
</script>
