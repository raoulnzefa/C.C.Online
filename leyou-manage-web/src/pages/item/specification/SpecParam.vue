<template>
    <div>
        <v-data-table
            :headers="headers"
            :items="params"
            hide-actions
            class="elevation-0"
            >
            <template slot="items" slot-scope="props">
                <td class="text-xs-center">{{ props.item.id }}</td>
                <td class="text-xs-center">{{ props.item.name }}</td>
                <td class="text-xs-center">{{ formatBoolean(props.item.numeric) }}</td>
                <td class="text-xs-center">{{ props.item.unit || 'null' }}</td>
                <td class="text-xs-center">{{ formatBoolean(props.item.generic) }}</td>
                <td class="text-xs-center">{{ formatBoolean(props.item.searching) }}</td>
                <td class="justify-center layout px-0">
                <v-btn  color='primary' icon @click="editParam(props.item)">
                    <i class="el-icon-edit"/>
                </v-btn>
                <v-btn color="warning" icon @click="deleteParam(props.item.id)">
                    <i class="el-icon-delete"/>
                </v-btn>
                </td>
            </template>
            <template slot="no-data">
                Bajo este grupo no hay parámetros
                </template>
            </v-data-table>
            <v-btn color='blue-grey darken-1'  style="color: white" @click="addParam">Añadir parámetro</v-btn>
            <v-dialog v-model="show" max-width="350px" scrollable>
            <v-card>
                <v-card-text style="height: 300px;">
                    <v-flex class="px-3">
                    <v-text-field label="Nombre de parámetro：" v-model="param.name"  />
                    <v-checkbox label="¿Es parámetro general?" v-model="param.generic" color="primary" hide-details/>
                    <v-checkbox label="¿Es numérico?" v-model="param.numeric" color="primary" hide-details/>
                    <v-text-field label="Unidad numérico" v-model="param.unit" v-if="param.numeric"/>
                    <v-checkbox label="¿Se usa para búsqueda?" v-model="param.searching" color="primary" hide-details/>
                    <v-flex v-if="param.searching && param.numeric" class="px-2">
                      Intervalo de filtro de búsqueda：
                        <v-layout row wrap v-for="(s,i) in param.segments" :key="i">
                            <v-flex xs5>
                                <v-text-field prefix="From: " v-model="s[0]" single-line hide-details/>
                            </v-flex>
                            <v-spacer/>
                            <v-flex xs5>
                                <v-text-field prefix="To: " v-model="s[1]" single-line hide-details/>
                            </v-flex>
                            <v-flex xs1>
                                <v-btn icon @click="param.segments.splice(i,1)">
                                     <i class="el-icon-delete"/>
                                </v-btn>
                            </v-flex>
                        </v-layout>
                        <v-layout row>
                            <v-spacer/>
                            <v-flex xs1>
                                <v-tooltip left>
                                <v-btn slot="activator" icon @click="param.segments.push([0,0])"><v-icon>add</v-icon></v-btn>
                                <span>Click para agregar segmentos de parámetros numéricos para facilitar la búsqueda y el filtro</span>
                                </v-tooltip>
                            </v-flex>
                        </v-layout>
                    </v-flex>
                    </v-flex>
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
  name: "v-spec-param",
  props: {
    group: {
      type: Object
    }
  },
  data() {
    return {
      headers: [
        { text: "ID", value: "id", align: "center", sortable: false },
        { text: "Nombre", value: "name", align: "center", sortable: false },
        { text: "Numérico", value: "numeric",
          align: "center",
          sortable: false
        },
        { text: "Unidad", value: "unit", align: "center", sortable: false },
        {
          text: "General",
          value: "generic",
          align: "center",
          sortable: false
        },
        {
          text: "¿Búsqueda?",
          value: "searching",
          align: "center",
          sortable: false
        },
        { text: "Op", align: "center", sortable: false }
      ],
      params: [], // 参数
      show: false,
      param: {},
      isEdit: false
    };
  },
  watch: {
    group:{
      deep:true,
      handler(val){
          if(val && val.id){
            this.loadData();
          }
      }
    }
  },
  methods: {
    loadData() {
      this.$http
        .get("/item/spec/params?gid=" + this.group.id)
        .then(({ data }) => {
          data.forEach(p => {
              p.segments = p.segments ? p.segments.split(",").map(s => s.split("-")) : [];
          })
          this.params = data;
        })
        .catch(() => {
          this.params = [];
        });
    },
    editParam(param) {
        this.param = param;
        this.isEdit = true;
        this.show = true;
    },
    addParam() {
      this.param = {
          cid: this.group.cid,
          groupId: this.group.id,
          segments:[],
          numeric:false,
          searching:false,
          generic:false}
      this.show = true;
    },
    deleteParam(id) {
        this.$message.confirm("¿Confirmar la eliminación de parámetros?")
        .then(() => {
            this.$http.delete("/item/spec/param/" + id)
            .then(() => {
                this.$message.success("¡Eliminado!");
                this.loadData();
            })
            .catch(() => {
                this.$message.error("¡Error!");
            })
        })
    },
    formatBoolean(boo) {
      return boo ? "SÍ" : "NO";
    },
    save(){
        const p = {};
        Object.assign(p, this.param);
        p.segments = p.segments.map(s => s.join("-")).join(",")
        this.$http({
            method: this.isEdit ? 'put' : 'post',
            url: '/item/spec/param',
            data: p,
        }).then(() => {
            // 关闭窗口
            this.show = false;
            this.$message.success("¡Guardado!");
            this.loadData();
          }).catch(() => {
              this.$message.error("¡Error!");
            });
    }
  }
};
</script>
