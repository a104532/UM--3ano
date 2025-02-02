using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataAccessLibrary.Models
{
    public class IngredientePasso
    {
        public int idIngrediente { get; set; }
        public int nrPasso { get; set; }
        public int idProduto { get; set; }
        public int quantidade { get; set; }
    }
}
