local root_dir = vim.fs.root(0, { "go.mod", "proto" }) or vim.fn.getcwd()

Pacha.setup_lsps({
  "buf_ls",
  "gopls",
}, {
  buf_ls = {
    root_dir = function(fname)
      return root_dir
    end,
  },
  gopls = {
    root_dir = function(fname)
      return root_dir
    end,
  },
})
