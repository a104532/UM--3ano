using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualBasic;

namespace DataAccessLibrary.Models
{

    public class Encomenda
    {
        public int idEncomenda{ get; set; }
        public DateTime dataEntrega{ get; set; }
        public string estado { get; set; }
        public string email {  get; set; }
        public int nrPasso { get; set; }
        public int idProduto { get; set; }

        public Encomenda()
        {
           
        }

        public Encomenda(DateTime dataEntrega, string email, int idProduto)
        {
            this.dataEntrega = dataEntrega;
            this.estado = "nao_iniciado";
            this.email = email;
            this.nrPasso = 1;
            this.idProduto = idProduto;
        }
    }

    

}
