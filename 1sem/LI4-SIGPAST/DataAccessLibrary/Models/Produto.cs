using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataAccessLibrary.Models
{
    public class Produto
    {
        public int idProduto {  get; set; }
        public string designacao { get; set; }
        public string descricao { get; set; }
        public string imagem { get; set; }
        public int nrPassos { get; set; }
    }
}
