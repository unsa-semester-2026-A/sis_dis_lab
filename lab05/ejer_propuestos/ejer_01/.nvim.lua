local root_dir = vim.fn.getcwd()
Pacha.setup_lsps({
  "jdtls",
  "buf_ls",
}, {
    jdtls = {
      root_dir = function(fname)
        return root_dir
      end,
      settings = {
        java = {
          project = {
            sourcePaths = { "src" }
          }
        }
      }
    }
  })
