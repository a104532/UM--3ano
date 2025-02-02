using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataAccessLibrary.Models
{
    public class PassoProducao
    {
        public int nrPasso { get; set; }
        public int idProduto { get; set; }
        public string texto { get; set; }
        public string imagem { get; set; }
    }
}
