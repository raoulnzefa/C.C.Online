<template>
    <div>
        <v-data-table
        :headers="headers"
        :items="groups"
        hide-actions
        class="elevation-0"
        >
            <template slot="items" slot-scope="props">
                <tr @click="selectGroup(props.item)">
                    <td class="text-xs-center">{{ props.item.id }}</td>
                    <td class="text-xs-center">{{ props.item.name }}</td>
                    <td class="justify-center layout px-0">
                    <v-btn icon @click.stop="editGroup(props.item)" color="primary">
                        <i class="el-icon-edit"/>
                    </v-btn>
                    <v-btn icon @click.stop="deleteGroup(props.item.id)" color="warning">
                        <i class="el-icon-delete"/>
                    </v-btn>
                    </td>
                </tr>
            </template>
            <template slot="no-data">
              Bajo este catálogo aún no hay grupo de especificaciones o no se ha elegido un catálogo
            </template>
        </v-data-table>

        <v-btn color='blue-grey darken-1'  style="color: white" @click="addGroup">Añadir grupo</v-btn>
        <v-dialog v-model="show" width="300" height="200">
        <v-card >
            <v-card-text>
                <v-text-field label="Nombre de grupo：" v-model="group.name"  />
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <v-btn color="blue darken-1" flat @click.native="show=false">Cancelar</v-btn>
                <v-btn color="blue darken-1" flat @click.native="save">Guardar</v-btn>
            </v-card-actions>
        </v-card>
        </v-dialog>
    </div>
</template>

<script>
export default {
  name: "spec-group",
  props: {
    cid: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      groups:[],
      headers: [
        { text: "ID", value: "id", align: "center", sortable: false },
        { text: "Nombre de Grupo", value: "name", align: "center", sortable: false },
        { text: "Op.", align: "center", sortable: false }
      ],
      show: false, // 是否打开编辑窗口
      group:{name:''},
      isEdit: false, // 是否是编辑
    };
  },
  watch:{
      cid(){
          this.loadData();
      }
  },
  methods:{
      loadData(){
          this.$http.get("/item/spec/groups/" + this.cid)
          .then(({data}) => {
              this.groups = data;
          })
          .catch(() => {
              this.groups = [];
          })
      },
      editGroup(group){
          Object.assign(this.group, group);
          this.show = true;
          this.isEdit = true;
      },
      addGroup(){
          this.group = {cid:this.cid};
          this.show = true;
          this.isEdit = false;
      },
      save(){
           this.$http({
            method: this.isEdit ? 'put' : 'post',
            url: '/item/spec/group',
            data: this.group
          }).then(() => {
            // 关闭窗口
            this.show = false;
            this.$message.success("¡Guardado！");
            this.loadData();
          }).catch(() => {
              this.$message.error("¡Error！");
            });
      },
      deleteGroup(id){
          this.$message.confirm("¿Confirmar la eliminación de grupo?")
          .then(() => {
            this.$http.delete("/item/spec/group/" + id)
                .then(() => {
                    this.$message.success("¡Eliminado!");
                    this.loadData();
                })
          })
      },
      selectGroup(group){
          this.$emit("select", group);
      }
  }
};
</script>
