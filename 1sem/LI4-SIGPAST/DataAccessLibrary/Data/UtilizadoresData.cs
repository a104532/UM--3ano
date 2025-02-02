using DataAccessLibrary.Interfaces;
using DataAccessLibrary.Models;

namespace DataAccessLibrary.Data
{
    public class UtilizadoresData : IUtilizadoresData
    {

        private readonly ISqlDataAccess _db;

        public UtilizadoresData(ISqlDataAccess db)
        {
            _db = db;
        }

        public Task<List<Utilizador>> GetAll()
        {
            string sql = "select * from dbo.Utilizador";

            return _db.LoadData<Utilizador, dynamic>(sql, new { });
        }

        public async Task insertUtilizador(Utilizador utilizador)
        {
            string sql = @"insert into dbo.Utilizador (email, password, nome, datanascimento, telefone, iban, morada, tipo)
                           values (@email, @password, @nome, @datanascimento, @telefone, @iban, @morada, @tipo);";

            await _db.saveData(sql, utilizador);
        }

        public async Task<Utilizador> findUtilizador(string email)
        {
            string sql = $"select * from dbo.Utilizador where email = '{email}'";

            List<Utilizador> u = await _db.LoadData<Utilizador,dynamic>(sql, new {Email = email });

            if (u.Count > 0)
                return u[0];
            else
                return null;
          
        }

    }
}
