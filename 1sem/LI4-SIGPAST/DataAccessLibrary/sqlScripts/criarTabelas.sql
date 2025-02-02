use sigpast;

create table Utilizador(
	email varchar(45) not null primary key ,
	password varchar(45) not null,
	nome varchar(45),
	dataNascimento date,
	telefone varchar(16),
	IBAN varchar(25),
	morada varchar(100),
	tipo varchar(20) not null check (tipo in ('funcionario','administrador','cliente'))
);

create table Produto(
	idProduto int identity(1,1) not null primary key,
	designacao varchar(45) not null,
	descricao varchar(500) not null,
	imagem varchar(100) not null,
	nrPassos int not null
);

create table PassoProducao(
	idProduto int not null foreign key references Produto(idProduto),
	nrPasso int not null,
	texto varchar(500) not null,
	imagem varchar(100),
	primary key (nrPasso, idProduto)
);

create table Encomenda(
	idEncomenda int identity(1,1) not null primary key,
	dataEntrega date not null,
	estado varchar(20) not null check (estado in('em_processo','nao_iniciado','finalizado')),
	nrPasso int not null,
	idProduto int not null foreign key references Produto(idProduto),
	email varchar(45) not null foreign key references Utilizador (email),
	foreign key (nrPasso, idProduto) references PassoProducao (nrPasso, idProduto),
);

create table Ingrediente(
	idIngrediente int identity(1,1) not null primary key,
	designacao varchar(45) not null,
	unidade varchar(10) not null,
	quantidadeStock float not null,
	imagem varchar(100) not null
);

create table IngredienteProduto(
	idIngrediente int not null foreign key references Ingrediente (idIngrediente),
	idProduto int not null foreign key references Produto (idProduto),
	quantidade float not null,
	primary key (idIngrediente, idProduto)
);

create table IngredientePassoProducao(
	idIngrediente int not null foreign key references Ingrediente (idIngrediente),
	nrPasso int not null,
	idProduto int not null,
	quantidade float not null,
	foreign key (nrPasso, idProduto) references PassoProducao (nrPasso, idProduto),
	primary key (idIngrediente, nrPasso, idProduto)
);