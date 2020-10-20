var menus = [
  {
    action: "home",
    title: "Home",
    path:"/index",
    items: [{ title: "Estadísticas", path: "/dashboard" }]
  },
  {
    action: "apps",
    title: "Gestión de Productos",
    path:"/item",
    items: [
      { title: "Gestión de Categorías", path: "/category" },
      { title: "Gestión de Marca", path: "/brand" },
      { title: "Listado de Productos", path: "/list" },
      { title: "Especificaciones", path: "/specification" }
    ]
  },
  {
    action: "people",
    title: "Gestión de Miembro",
    path:"/user",
    items: [
      { title: "Estadísticas de Miembro", path: "/statistics" },
      { title: "Gestión de Miembro", path: "/list" }
    ]
  },
  {
    action: "attach_money",
    title: "Gestión de Ventas",
    path:"/trade",
    items: [
      { title: "Estadísticas de Transacciones", path: "/statistics" },
      { title: "Gestión de Pedidos", path: "/order" },
      { title: "Gestión de Logística", path: "/logistics" },
      { title: "Gestión de Promociones", path: "/promotion" }
    ]
  },
  {
    action: "settings",
    title: "Gestión de Autoridad",
    path:"/authority",
    items: [
      { title: "Autoridad", path: "/list" },
      { title: "Roles", path: "/role" },
      { title: "Personal", path: "/member" }
    ]
  }
]

export default menus;
