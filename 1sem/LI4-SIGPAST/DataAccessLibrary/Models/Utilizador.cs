using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataAccessLibrary.Models
{
    public class Utilizador
    {
        public string email { get; set; }
        public string password { get; set; }
        public string nome { get; set; }
        public DateTime dataNascimento { get; set; }
        public string telefone { get; set; }
        public string iban { get; set; }
        public string morada { get; set; }
        public string tipo { get; set; }

        public Utilizador() { }

        public Utilizador(string email, string password, string nome, DateTime dataNascimento, string telefone, string iban, string morada, string tipo)
        {
            this.email = email;
            this.password = password;
            this.nome = nome;
            this.dataNascimento = dataNascimento;
            this.telefone = telefone;
            this.iban = iban;
            this.morada = morada;
            this.tipo = tipo;
        }
    }
}
