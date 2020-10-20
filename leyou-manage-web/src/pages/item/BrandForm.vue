<template>
  <v-form v-model="valid" ref="myBrandForm">
    <v-text-field v-model="brand.name" label="Insertar el nombre de la marca" required :rules="nameRules"/>
    <v-text-field v-model="brand.letter" label="Insertar la primera letra de Marca" required :rules="letterRules"/>
    <v-cascader
      url="/item/category/list"
      multiple
      required
      v-model="brand.categories"
      label="Seleccionar la categoría de la marca"/>
    <v-layout row>
      <v-flex xs3>
        <span style="font-size: 16px; color: #444">Marca LOGO：</span>
      </v-flex>
      <v-flex>
        <v-upload
          v-model="brand.image" url="/upload/image" :multiple="false" :pic-width="250" :pic-height="90"
        />
      </v-flex>
    </v-layout>
    <v-layout class="my-4" row>
      <v-spacer/>
      <v-btn @click="submit" color="blue-grey darken-1" style="color: white" small>Entregar</v-btn>
      <v-btn @click="clear" small>Resetear</v-btn>
    </v-layout>
  </v-form>
</template>

<script>
export default {
  name: "brand-form",
  /*子组件接收标记*/
  props: {
    oldBrand: {
      type: Object
    },
    isEdit: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      valid: false, // 表单校验结果标记
      brand: {
        name: '', // 品牌名称
        letter: '', // 品牌首字母
        image: '',// 品牌logo
        categories: [], // 品牌所属的商品分类数组

      },
      nameRules: [
        v => !!v || "Nombre de marca no puede ser nulo",
        v => v.length > 1 || "Al menos 2 letras"
      ],
      letterRules: [
        v => !!v || "Inicial no puede ser nulo",
        v => /^[a-zA-Z]{1}$/.test(v) || "Sólo una letra"
      ]
    }
  },
  methods: {
    submit() {
      // 表单校验
      if (this.$refs.myBrandForm.validate()) {
        // 定义一个请求参数对象，通过解构表达式来获取brand中的属性
        const {categories, letter, ...params} = this.brand;
        // 数据库中只要保存分类的id即可，因此我们对categories的值进行处理,只保留id，并转为字符串
        params.cids = categories.map(c => c.id).join(",");
        // 将字母都处理为大写
        params.letter = letter.toUpperCase();
        // 将数据提交到后台
        // this.$http.post('/item/brand', this.$qs.stringify(params))
        this.$http({
          method: this.isEdit ? 'put' : 'post',
          url: '/item/brand',
          data: this.$qs.stringify(params)
        }).then(() => {
          // 关闭窗口
          this.$emit("close");
          this.$message.success("Guardado con éxito！");
        }).catch(() => {
          this.$message.error("Guarda con fallo！");
        });
      }
    },
    clear() {
      // 重置表单
      this.$refs.myBrandForm.reset();
      // 需要手动清空商品分类
      this.categories = [];
    }
  },
  watch: {
    oldBrand: {// 监控oldBrand的变化
      handler(val) {
        if (val) {
          // 注意不要直接复制，否则这边的修改会影响到父组件的数据，copy属性即可
          this.brand = Object.deepCopy(val)
        } else {
          // 为空，初始化brand
          this.brand = {
            name: '',
            letter: '',
            image: '',
            categories: [],
          }
        }
      },
      deep: true
    }
  }
}
</script>

<style scoped>

</style>
