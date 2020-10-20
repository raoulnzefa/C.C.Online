<template>
  <v-card>
    <v-flex xs12 sm10>
      <v-tree url="/item/category/list"
              :isEdit="isEdit"
              @handleAdd="handleAdd"
              @handleEdit="handleEdit"
              @handleDelete="handleDelete"
              @handleClick="handleClick"
      />
    </v-flex>
  </v-card>
</template>

<script>
export default {
  name: "category",
  data() {
    return {
      isEdit: true,
    }
  },
  methods: {
    handleAdd(node) {
      this.$http({
        method: 'post',
        url: '/item/category',
        data: this.$qs.stringify(node)
      }).then(() => {
        this.reloadData(node.id);
      });
    },
    handleEdit(id, name) {
      const node = {
        id: id,
        name: name
      };
      this.$http({
        method: 'put',
        url: '/item/category',
        data: this.$qs.stringify(node),
      }).then(() => {
        this.$message.info("Editado con éxito！");
      }).catch(() => {
        this.$message.info("Editar con fallo！");
      });
    },
    handleDelete(id) {
      this.$http.delete("/item/category/cid/" + id).then(() => {
        this.$message.info("Eliminado！");
      }).catch(() => {
        this.$message.info("Fallado！");
      })
    },
    handleClick(node) {
      console.log(node)
    },
    reloadData(id) {
      this.$http.get("/item/category/list?pid=" + id).then().catch();
    }
  }
}
</script>

<style scoped>

</style>
