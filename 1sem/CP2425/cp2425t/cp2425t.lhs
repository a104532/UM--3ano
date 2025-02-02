\documentclass[11pt, a4paper, fleqn]{article}
\usepackage{cp2425t}
\makeindex

%================= lhs2tex=====================================================%
%include polycode.fmt
%format (div (x)(y)) = x "\div " y
%format succ = "\succ "
%format ==> = "\Longrightarrow "
%format map = "\map "
%format length = "\length "
%format fst = "\p1"
%format p1  = "\p1"
%format snd = "\p2"
%format p2  = "\p2"
%format Left = "i_1"
%format Right = "i_2"
%format i1 = "i_1"
%format i2 = "i_2"
%format >< = "\times"
%format >|<  = "\bowtie "
%format |-> = "\mapsto"
%format . = "\comp "
%format .=?=. = "\mathbin{\stackrel{\mathrm{?}}{=}}"
%format (kcomp (f)(g)) = f "\kcomp " g
%format -|- = "+"
%format conc = "\mathsf{conc}"
%format summation = "{\sum}"
%format (either (a) (b)) = "\alt{" a "}{" b "}"
%format (frac (a) (b)) = "\frac{" a "}{" b "}"
%format (uncurry f) = "\uncurry{" f "}"
%format (const (f)) = "\underline{" f "}"
%format TLTree = "\mathsf{TLTree}"
%format (Seq (a)) = "{" a "}^{*}"
%format (lcbr (x)(y)) = "\begin{lcbr}" x "\\" y "\end{lcbr}"
%format (lcbr3 (x)(y)(z)) = "\begin{lcbr}" x "\\" y "\\" z "\end{lcbr}"
%format (split (x) (y)) = "\conj{" x "}{" y "}"
%format (for (f) (i)) = "\for{" f "}\ {" i "}"
%format B_tree = "\mathsf{B}\mbox{-}\mathsf{tree} "
%format <$> = "\mathbin{\mathopen{\langle}\$\mathclose{\rangle}}"
%format Either a b = a "+" b
%format fmap = "\mathsf{fmap}"
%format NA   = "\textsc{na}"
%format NB   = "\textbf{NB}"
%format inT = "\mathsf{in}"
%format outT = "\mathsf{out}"
%format outLTree = "\mathsf{out}"
%format inLTree = "\mathsf{in}"
%format inFTree = "\mathsf{in}"
%format outFTree = "\mathsf{out}"
%format Null = "1"
%format (Prod (a) (b)) = a >< b
%format fF = "\fun F "
%format l2 = "l_2 "
%format Dist = "\fun{Dist}"
%format IO = "\fun{IO}"
%format LTree = "{\LTree}"
%format FTree = "{\FTree}"
%format inNat = "\mathsf{in}"
%format (cata (f)) = "\llparenthesis\, " f "\,\rrparenthesis"
%format (cataNat (g)) = "\cataNat{" g "}"
%format (cataList (g)) = "\llparenthesis\, " g "\,\rrparenthesis"
%format (cataLTree (x)) = "\llparenthesis\, " x "\,\rrparenthesis"
%format (cataFTree (x)) = "\llparenthesis\, " x "\,\rrparenthesis"
%format (cataRose (x)) = "\llparenthesis\, " x "\,\rrparenthesis_\textit{\tiny R}"
%format (ana (g)) = "\ana{" g "}"
%format (anaList (g)) = "\anaList{" g "}"
%format (anaLTree (g)) = "\lanabracket\," g "\,\ranabracket"
%format (anaRose (g)) = "\lanabracket\," g "\,\ranabracket_\textit{\tiny R}"
%format (hylo (g) (h)) = "\llbracket\, " g ",\," h "\,\rrbracket"
%format (hyloRose (g) (h)) = "\llbracket\, " g ",\," h "\,\rrbracket_\textit{\tiny R}"
%format Nat0 = "\N_0"
%format Rational = "\Q "
%format toRational = " to_\Q "
%format fromRational = " from_\Q "
%format muB = "\mu "
%format (frac (n)(m)) = "\frac{" n "}{" m "}"
%format (fac (n)) = "{" n "!}"
%format (underbrace (t) (p)) = "\underbrace{" t "}_{" p "}"
%format matrix = "matrix"
%format `ominus` = "\mathbin{\ominus}"
%format % = "\mathbin{/}"
%format <-> = "{\,\leftrightarrow\,}"
%format <|> = "{\,\updownarrow\,}"
%format `minusNat`= "\mathbin{-}"
%format ==> = "\Rightarrow"
%format .==>. = "\Rightarrow"
%format .<==>. = "\Leftrightarrow"
%format .==. = "\equiv"
%format .<=. = "\leq"
%format .&&&. = "\wedge"
%format cdots = "\cdots "
%format pi = "\pi "
%format (curry (f)) = "\overline{" f "}"
%format delta = "\Delta "
%format (plus (f)(g)) = "{" f "}\plus{" g "}"
%format ++ = "\mathbin{+\!\!+}"
%format Integer  = "\mathbb{Z}"
%format (Cp.cond (p) (f) (g)) = "\mcond{" p "}{" f "}{" g "}"
%format (square (x)) = x "^2"

%format (cataTree (f) (g)) = "\llparenthesis\, " f "\:" g "\,\rrparenthesis_{\textit{\tiny T}}"
%format (cataForest (f) (g)) = "\llparenthesis\, " f "\:" g "\,\rrparenthesis_{\textit{\tiny F}}"
%format (anaTree (f) (g)) = "\lanabracket\;\!" f "\:" g "\:\!\ranabracket_{\textit{\tiny T}}"
%format (anaForest (f) (g)) = "\lanabracket\;\!" f "\:" g "\:\!\ranabracket_{\textit{\tiny F}}"
%format (hyloTree (ft) (ff) (gt) (gf)) = "\llbracket\, " ft "\:" ff ",\," gt "\:" gf "\,\rrbracket_{\textit{\tiny T}}"
%format (hyloForest (ft) (ff) (gt) (gf)) = "\llbracket\, " ft "\:" ff ",\," gt "\:" gf "\,\rrbracket_{\textit{\tiny F}}"
%format inTree = "\mathsf{in}_{Tree}"
%format inForest = "\mathsf{in}_{Forest}"
%format outTree = "\mathsf{out}_{Tree}"
%format outForest = "\mathsf{out}_{Forest}"

%format (cata' (f) (g)) = "\llparenthesis\, " f "\:" g "\,\rrparenthesis"
%format (ana' (f) (g)) = "\lanabracket\;\!" f "\:" g "\:\!\ranabracket"
%format (hylo' (ft) (ff) (gt) (gf)) = "\llbracket\, " ft "\:" ff ",\," gt "\:" gf "\,\rrbracket"
%format .* = "\star " 
%------------------------------------------------------------------------------%


%====== DEFINIR GRUPO E ELEMENTOS =============================================%

\group{G99}
\studentA{104095}{Rafael Airosa Pereira }
\studentB{104532}{Tomás Sousa Barbosa }
\studentC{104272}{João Miguel Freitas Rodrigues }

%==============================================================================%

\begin{document}

\sffamily
\setlength{\parindent}{0em}
\emergencystretch 3em
\renewcommand{\baselinestretch}{1.25} 
\input{Cover}
\pagestyle{pagestyle}
\setlength{\parindent}{1em}
\newgeometry{left=25mm,right=20mm,top=25mm,bottom=25mm}

\section*{Preâmbulo}

Em \CP\ pretende-se ensinar a progra\-mação de computadores como uma disciplina
científica. Para isso parte-se de um repertório de \emph{combinadores} que
formam uma álgebra da programação % (conjunto de leis universais e seus corolários)
e usam-se esses combinadores para construir programas \emph{composicionalmente},
isto é, agregando programas já existentes.

Na sequência pedagógica dos planos de estudo dos cursos que têm esta disciplina,
opta-se pela aplicação deste método à programação em \Haskell\ (sem prejuízo
da sua aplicação a outras linguagens funcionais). Assim, o presente trabalho
prático coloca os alunos perante problemas concretos que deverão ser implementados
em \Haskell. Há ainda um outro objectivo: o de ensinar a documentar programas,
a validá-los e a produzir textos técnico-científicos de qualidade.

Antes de abordarem os problemas propostos no trabalho, os grupos devem ler
com atenção o anexo \ref{sec:documentacao} onde encontrarão as instruções
relativas ao \emph{software} a instalar, etc.

Valoriza-se a escrita de \emph{pouco} código que corresponda a soluções simples
e elegantes que utilizem os combinadores de ordem superior estudados na disciplina.

%if False
\begin{code}
{-# OPTIONS_GHC -XNPlusKPatterns #-}
{-# LANGUAGE GeneralizedNewtypeDeriving, DeriveDataTypeable, FlexibleInstances #-}
module Main where
import Cp
import List hiding (fac)
import Nat hiding (aux)
import LTree
import FTree
import Exp
-- import Probability
import Data.List hiding (find)
-- import Svg hiding (for,dup,fdiv)
import Control.Monad
import Control.Applicative hiding ((<|>))
import System.Process
import Data.Char
import Data.Ratio
import Control.Concurrent


main = undefined
\end{code}
%endif

\Problema

Esta questão aborda um problema que é conhecido pela designação '\emph{H-index of a Histogram}'
e que se formula facilmente:
\begin{quote}\em
O h-index de um histograma é o maior número |n| de barras do histograma cuja altura é maior ou igual a |n|.
\end{quote}
Por exemplo, o histograma 
\begin{code}
h = [5,2,7,1,8,6,4,9]
\end{code}
que se mostra na figura
	\histograma
tem |hindex h = 5|
pois há |5| colunas maiores que |5|. (Não é |6| pois maiores ou iguais que seis só há quatro.)

Pretende-se definida como um catamorfismo, anamorfismo ou hilomorfismo uma função em Haskell
\begin{code}
hindex :: [Int] -> (Int,[Int])
\end{code}
tal que, para |(i,x) = hindex h|, |i| é o H-index de |h| e |x| é a lista de colunas de |h| que para ele contribuem.

A proposta de |hindex| deverá vir acompanhada de um \textbf{diagrama} ilustrativo.

\Problema

Pelo \href{https://en.wikipedia.org/wiki/Fundamental_theorem_of_arithmetic}{teorema
fundamental da aritmética}, todo número inteiro positivo tem uma única factorização
prima.  For exemplo,
\begin{verbatim}
primes 455
[5,7,13]
primes 433
[433]
primes 230
[2,5,23]
\end{verbatim}

\begin{enumerate}

\item	
Implemente como anamorfismo de listas a função
\begin{code}
primes :: Integer -> [Integer] 
\end{code}
que deverá, recebendo um número inteiro positivo, devolver a respectiva lista
de factores primos.

A proposta de |primes| deverá vir acompanhada de um \textbf{diagrama} ilustrativo.

\item A figura mostra a ``\emph{árvore dos primos}'' dos números |[455,669,6645,34,12,2]|.

      \primes

Com base na alínea anterior, implemente uma função em Haskell que faça a
geração de uma tal árvore a partir de uma lista de inteiros:

\begin{code}
prime_tree :: [Integer] -> Exp Integer Integer
\end{code}

\textbf{Sugestão}: escreva o mínimo de código possível em |prime_tree| investigando
cuidadosamente que funções disponíveis nas bibliotecas que são dadas podem
ser reutilizadas.%
\footnote{Pense sempre na sua produtividade quando está a programar --- essa
atitude será valorizada por qualquer empregador que vier a ter.}

\end{enumerate}

\Problema

A convolução |a .* b| de duas listas |a| e |b| --- uma operação relevante em computação
---  está muito bem explicada
\href{https://www.youtube.com/watch?v=KuXjwB4LzSA}{neste vídeo} do canal
\textbf{3Blue1Brown} do YouTube,
a partir de \href{https://www.youtube.com/watch?v=KuXjwB4LzSA&t=390s}{|t=6:30|}.
Aí se mostra como, por exemplo:
\begin{quote}
|[1,2,3] .* [4,5,6] = [4,13,28,27,18]| 
\end{quote}
A solução abaixo, proposta pelo chatGPT,
\begin{spec}
convolve :: Num a => [a] -> [a] -> [a]
convolve xs ys = [sum $ zipWith (*) (take n (drop i xs)) ys | i <- [0..(length xs - n)]]
  where n = length ys 
\end{spec}
está manifestamente errada, pois |convolve [1,2,3] [4,5,6] = [32]| (!).

Proponha, explicando-a devidamente, uma solução sua para |convolve|.
Valorizar-se-á a economia de código e o recurso aos combinadores \emph{pointfree} estudados
na disciplina, em particular a triologia \emph{ana-cata-hilo} de tipos disponíveis nas
bibliotecas dadas ou a definir.

\Problema

Considere-se a seguinte sintaxe (abstrata e simplificada) para \textbf{expressões numéricas} (em |b|) com variáveis (em |a|),
\begin{code}
data Expr b a =   V a | N b | T Op [ Expr b a ]  deriving (Show,Eq)

data Op = ITE | Add | Mul | Suc deriving (Show,Eq)
\end{code}
possivelmente condicionais (cf.\ |ITE|, i.e.\ o operador condicional ``if-then-else``).
Por exemplo, a árvore mostrada a seguir
        \treeA
representa a expressão
\begin{eqnarray}
        |ite (V "x") (N 0) (multi (V "y") (soma (N 3) (V "y")))|
        \label{eq:expr}
\end{eqnarray}
--- i.e.\ |if x then 0 else y*(3+y)| ---
assumindo as ``helper functions'':
\begin{code}
soma  x y = T Add [x,y]
multi x y = T Mul [x,y]
ite x y z = T ITE [x,y,z]
\end{code}

No anexo \ref{sec:codigo} propôe-se uma base para o tipo Expr (|baseExpr|) e a 
correspondente algebra |inExpr| para construção do tipo |Expr|.

\begin{enumerate}
\item        Complete as restantes definições da biblioteca |Expr|  pedidas no anexo \ref{sec:resolucao}.
\item        No mesmo anexo, declare |Expr b| como instância da classe |Monad|. \textbf{Sugestão}: relembre os exercícios da ficha 12.
\item        Defina como um catamorfismo de |Expr| a sua versão monádia, que deverá ter o tipo:
\begin{code}
mcataExpr :: Monad m => (Either a (Either b (Op, m [c])) -> m c) -> Expr b a -> m c
\end{code}
\item        Para se avaliar uma expressão é preciso que todas as suas variáveis estejam instanciadas.
Complete a definição da função
\begin{code}
let_exp :: (Num c) => (a -> Expr c b) -> Expr c a -> Expr c b
\end{code}
que, dada uma expressão com variáveis em |a| e uma função que a cada uma dessas variáveis atribui uma
expressão (|a -> Expr c b|), faz a correspondente substituição.\footnote{Cf.\ expressões |let ... in ...|.}
Por exemplo, dada
\begin{code}
f "x" = N 0
f "y" = N 5
f _   = N 99
\end{code}
ter-se-á
\begin{spec}
        let_exp f e = T ITE [N 1,N 0,T Mul [N 5,T Add [N 3,N 1]]]
\end{spec}
isto é, a árvore da figura a seguir: 
        \treeB

\item Finalmente, defina a função de avaliação de uma expressão, com tipo

\begin{code}
evaluate :: (Num a, Ord a) =>  Expr a b -> Maybe a
\end{code}

que deverá ter em conta as seguintes situações de erro:

\begin{enumerate}

\item \emph{Variáveis} --- para ser avaliada, |x| em |evaluate x| não pode conter variáveis. Assim, por exemplo,
        \begin{spec}
        evaluate e = Nothing
        evaluate (let_exp f e) = Just 40
        \end{spec}
para |f| e |e|  dadas acima.

\item \emph{Aridades} --- todas as ocorrências dos operadores deverão ter
      o devido número de sub-expressões, por exemplo:
        \begin{spec}
        evaluate (T Add [ N 2, N 3]) = Just 5
        evaluate (T Mul [ N 2 ]) = Nothing
        \end{spec}

\end{enumerate}

\end{enumerate}

\noindent
\textbf{Sugestão}: de novo se insiste na escrita do mínimo de código possível,
tirando partido da riqueza estrutural do tipo |Expr| que é assunto desta questão.
Sugere-se também o recurso a diagramas para explicar as soluções propostas.

\part*{Anexos}

\appendix

\section{Natureza do trabalho a realizar}
\label{sec:documentacao}
Este trabalho teórico-prático deve ser realizado por grupos de 3 alunos.
Os detalhes da avaliação (datas para submissão do relatório e sua defesa
oral) são os que forem publicados na \cp{página da disciplina} na \emph{internet}.

Recomenda-se uma abordagem participativa dos membros do grupo em \textbf{todos}
os exercícios do trabalho, para assim poderem responder a qualquer questão
colocada na \emph{defesa oral} do relatório.

Para cumprir de forma integrada os objectivos do trabalho vamos recorrer
a uma técnica de programa\-ção dita ``\litp{literária}'' \cite{Kn92}, cujo
princípio base é o seguinte:
%
\begin{quote}\em
	Um programa e a sua documentação devem coincidir.
\end{quote}
%
Por outras palavras, o \textbf{código fonte} e a \textbf{documentação} de um
programa deverão estar no mesmo ficheiro.

O ficheiro \texttt{cp2425t.pdf} que está a ler é já um exemplo de
\litp{programação literária}: foi gerado a partir do texto fonte
\texttt{cp2425t.lhs}\footnote{O sufixo `lhs' quer dizer
\emph{\lhaskell{literate Haskell}}.} que encontrará no \MaterialPedagogico\
desta disciplina des\-com\-pactando o ficheiro \texttt{cp2425t.zip}.

Como se mostra no esquema abaixo, de um único ficheiro (|lhs|)
gera-se um PDF ou faz-se a interpretação do código \Haskell\ que ele inclui:

	\esquema

Vê-se assim que, para além do \GHCi, serão necessários os executáveis \PdfLatex\ e
\LhsToTeX. Para facilitar a instalação e evitar problemas de versões e
conflitos com sistemas operativos, é recomendado o uso do \Docker\ tal como
a seguir se descreve.

\section{Docker} \label{sec:docker}

Recomenda-se o uso do \container\ cuja imagem é gerada pelo \Docker\ a partir do ficheiro
\texttt{Dockerfile} que se encontra na diretoria que resulta de descompactar
\texttt{cp2425t.zip}. Este \container\ deverá ser usado na execução
do \GHCi\ e dos comandos relativos ao \Latex. (Ver também a \texttt{Makefile}
que é disponibilizada.)

Após \href{https://docs.docker.com/engine/install/}{instalar o Docker} e
descarregar o referido zip com o código fonte do trabalho,
basta executar os seguintes comandos:
\begin{Verbatim}[fontsize=\small]
    $ docker build -t cp2425t .
    $ docker run -v ${PWD}:/cp2425t -it cp2425t
\end{Verbatim}
\textbf{NB}: O objetivo é que o container\ seja usado \emph{apenas} 
para executar o \GHCi\ e os comandos relativos ao \Latex.
Deste modo, é criado um \textit{volume} (cf.\ a opção \texttt{-v \$\{PWD\}:/cp2425t}) 
que permite que a diretoria em que se encontra na sua máquina local 
e a diretoria \texttt{/cp2425t} no \container\ sejam partilhadas.

Pretende-se então que visualize/edite os ficheiros na sua máquina local e que
os compile no \container, executando:
\begin{Verbatim}[fontsize=\small]
    $ lhs2TeX cp2425t.lhs > cp2425t.tex
    $ pdflatex cp2425t
\end{Verbatim}
\LhsToTeX\ é o pre-processador que faz ``pretty printing'' de código Haskell
em \Latex\ e que faz parte já do \container. Alternativamente, basta executar
\begin{Verbatim}[fontsize=\small]
    $ make
\end{Verbatim}
para obter o mesmo efeito que acima.

Por outro lado, o mesmo ficheiro \texttt{cp2425t.lhs} é executável e contém
o ``kit'' básico, escrito em \Haskell, para realizar o trabalho. Basta executar
\begin{Verbatim}[fontsize=\small]
    $ ghci cp2425t.lhs
\end{Verbatim}

\noindent Abra o ficheiro \texttt{cp2425t.lhs} no seu editor de texto preferido
e verifique que assim é: todo o texto que se encontra dentro do ambiente
\begin{quote}\small\tt
\verb!\begin{code}!
\\ ... \\
\verb!\end{code}!
\end{quote}
é seleccionado pelo \GHCi\ para ser executado.

\section{Em que consiste o TP}

Em que consiste, então, o \emph{relatório} a que se referiu acima?
É a edição do texto que está a ser lido, preenchendo o anexo \ref{sec:resolucao}
com as respostas. O relatório deverá conter ainda a identificação dos membros
do grupo de trabalho, no local respectivo da folha de rosto.

Para gerar o PDF integral do relatório deve-se ainda correr os comando seguintes,
que actualizam a bibliografia (com \Bibtex) e o índice remissivo (com \Makeindex),
\begin{Verbatim}[fontsize=\small]
    $ bibtex cp2425t.aux
    $ makeindex cp2425t.idx
\end{Verbatim}
e recompilar o texto como acima se indicou. (Como já se disse, pode fazê-lo
correndo simplesmente \texttt{make} no \container.)

No anexo \ref{sec:codigo} disponibiliza-se algum código \Haskell\ relativo
aos problemas que são colocados. Esse anexo deverá ser consultado e analisado
à medida que isso for necessário.

Deve ser feito uso da \litp{programação literária} para documentar bem o código que se
desenvolver, em particular fazendo diagramas explicativos do que foi feito e
tal como se explica no anexo \ref{sec:diagramas} que se segue.

\section{Como exprimir cálculos e diagramas em LaTeX/lhs2TeX} \label{sec:diagramas}

Como primeiro exemplo, estudar o texto fonte (\lhstotex{lhs}) do que está a ler\footnote{
Procure e.g.\ por \texttt{"sec:diagramas"}.} onde se obtém o efeito seguinte:\footnote{Exemplos
tirados de \cite{Ol18}.}
\begin{eqnarray*}
\start
|
	id = split f g
|
\just\equiv{ universal property }
|
     lcbr(
          p1 . id = f
     )(
          p2 . id = g
     )
|
\just\equiv{ identity }
|
     lcbr(
          p1 = f
     )(
          p2 = g
     )
|
\qed
\end{eqnarray*}

Os diagramas podem ser produzidos recorrendo à \emph{package} \Xymatrix, por exemplo:
\begin{eqnarray*}
\xymatrix@@C=2cm{
    |Nat0|
           \ar[d]_-{|cataNat g|}
&
    |1 + Nat0|
           \ar[d]^{|id + (cataNat g)|}
           \ar[l]_-{|inNat|}
\\
     |B|
&
     |1 + B|
           \ar[l]^-{|g|}
}
\end{eqnarray*}

\section{Código fornecido}\label{sec:codigo}

\subsection*{Problema 1}

\begin{code}
h :: [Int]
\end{code}

\subsection*{Problema 4}
Definição do tipo:
\begin{code}
inExpr :: Either a (Either b (Op, [Expr b a])) -> Expr b a
inExpr = either V (either N (uncurry T))
baseExpr :: (a1 -> b1) -> (a2 -> b2) -> (a3 -> b3) -> Either a1 (Either a2 (b4, [a3])) -> Either b1 (Either b2 (b4, [b3]))
baseExpr g h f = g -|- (h -|- id >< map f)
\end{code}
Exemplos de expressões:
\begin{code}
e = ite (V "x") (N 0) (multi (V "y") (soma (N 3) (V "y")))
i = ite (V "x") (N 1) (multi (V "y") (soma (N (3%5)) (V "y")))
\end{code}
Exemplo de teste:
\begin{code}
teste = evaluate (let_exp f i) == Just (26 % 245)
    where f "x" = N 0 ; f "y" = N (1%7)
\end{code}

%----------------- Soluções dos alunos -----------------------------------------%

\section{Soluções dos alunos}\label{sec:resolucao}
Os alunos devem colocar neste anexo as suas soluções para os exercícios
propostos, de acordo com o ``layout'' que se fornece.
Não podem ser alterados os nomes ou tipos das funções dadas, mas pode ser
adicionado texto ao anexo, bem como diagramas e/ou outras funções auxiliares
que sejam necessárias.

\noindent
\textbf{Importante}: Não pode ser alterado o texto deste ficheiro fora deste anexo.

\subsection*{Problema 1}

\begin{code}
hindex = cataList gene . iSort
  where
    gene :: Either () (Int, (Int, [Int])) -> (Int, [Int])
    gene (Left _) = (0, [])  
    gene (Right (x, (h, l)))
      | x >= length l + 1 = (length l + 1, x:l)  
      | x >= h = (h, x:l)  
      | otherwise = (h, l)

\end{code}
Este problema foi resolvido através de um catamorfismo da função gene, através do seguinte diagrama: 
        \hindexF

A ideia por detrás desta resolução é a seguinte:
\begin{itemize}
\item 1. Organizamos o array que foi dado por ordem crescente.
\item 2. Retiramos a cabeça do array e usámo-la como candidato a resposta.
\item 3. Caso a length do array seja maior do que o candidato, então quer dizer que existem um número maior ou igual de números maiores que o candidato.
\item 4. O candidato passa a ser a nossa nova resposta.
\item 5. Repetimos o processo até o tamanho do array ser menor do que o valor do candidato. Quando isso acontecer, então devolvemos a resposta atual e o array final.
\end{itemize}

\subsection*{Problema 2}
Primeira parte:
\begin{code}
primes = anaList gene 

gene :: Integer -> Either () (Integer, Integer)
gene = (id -|- succ_id) . outN0
  where
    outN0 :: Integer -> Either () Integer
    outN0 = cond (>1) (i2) (const (i1 ())) 
    
    succ_id :: Integer -> (Integer,Integer)
    succ_id = split smallestPrimeFactor (\n -> div n (smallestPrimeFactor n))
    
    smallestPrimeFactor :: Integer -> Integer
    smallestPrimeFactor n = head [x | x <- [2..n], n `mod` x == 0]

\end{code}
Este problema foi resolvido através de um anamorfismo da função $\mathtt{gene}$ e do seu diagrama:
        \primesF

A função $\mathtt{gene}$ é a função geradora que cria a lista de inteiros a partir de um inteiro, sendo composta por duas funções:

\begin{itemize}
\item A função $\mathtt{outN0}$, que verifica se o número é maior que 1, já que um número primo não pode ser negativo.
\item A função $\mathtt{succ\_id}$, que divide o número pelo seu menor fator primo.
\end{itemize}

Além disso, implementamos mais uma função: \\
A função $\mathtt{smallestPrimeFactor}$, uma função auxiliar que encontra o menor fator primo de um número.

Segunda parte:
\begin{code}
prime_tree xs = Term 1 (untar (zip  (map primes xs) xs)) 

\end{code}
Esta solução foi alcançada depois de estudar a função untar que nos foi disponibilizada na biblioteca "Exp.hs". \\
Explicando melhor o código:
\begin{itemize}
\item A função $\mathtt{primes}$ é aplicada a cada elemento da lista xs, criando uma lista de listas de primos.
\item A função $\mathtt{zip}$ junta as duas listas, criando uma lista de pares (lista de primos, valor inicial).
\item A função $\mathtt{untar}$ transforma o par numa Exp tree.
\item A função $\mathtt{Term 1}$ coloca o 1 na raiz da árvore, este tem que ser posto "manualmente", pois ele não é um número primo.
\end{itemize}

\subsection*{Problema 3}

\begin{code}
type State a = ([a], [a], Int)

matrixGen :: Num a => State a -> Either () ([a], State a)
matrixGen (xs, ys, i)
    | i >= length ys  = i1 ()  
    | otherwise = i2 (row, (xs, ys, i + 1))  
    where row = [x * (ys !! i) | x <- xs] 

getDiagonal :: Int -> [[a]] -> [a]
getDiagonal k matrix 
    | k < 0     = [] 
    | otherwise = [matrix !! i !! j 
      | i <- [0..rows-1], j <- [0..cols-1], i + j == k]
    where
        rows = length matrix
        cols = length (head matrix)

sumDiagonals :: Num a => [[a]] -> [a]
sumDiagonals matrix = map (\k -> sum (getDiagonal k matrix)) [0..(rows + cols - 2)]
    where
        rows = length matrix
        cols = length (head matrix)

convolve :: Num a => [a] -> [a] -> [a]
convolve xs ys = sumDiagonals (anaList matrixGen (xs, ys, 0) )
\end{code}

Decidimos implementar um type State a para facilitar o encapsulamento do estado necessário para gerar a matriz onde os dois primeiros
 argumentos apresentam as duas listas e o terceiro argumento um indice para controlar a iteração e indicar a linha da matriz a ser gerada.

Esta função foi implementada utilizando um anamorfismo da função $\mathtt{matrixGen}$, seguido pela aplicação da função $\mathtt{sumDiagonals}$.

O método de resolução foi inspirado no vídeo do canal 3Blue1Brown, que aborda a convolução de duas listas, relativamente à parte da utilização de uma matriz de modo a tornar a função mais eficiente (O(N.log(N))).

Explicação de forma simplificada:
\begin{itemize}
\item A função $\mathtt{matrixGen}$ é a função geradora responsável por criar uma única linha de uma matriz.
\item O anamorfismo dessa função gera todas as linhas da matriz, formando a estrutura completa.
\item Em seguida, a função $\mathtt{sumDiagonals}$ soma as diagonais da matriz e adiciona o resultado de cada soma a um array, que é então retornado como resultado final.
\end{itemize}

\subsection*{Problema 4}
\subsection*{Resposta à Pergunta 1}
\begin{code}
outExpr :: Expr b a -> Either a (Either b (Op, [Expr b a]))
outExpr (V a) = i1 a
outExpr (N b) = i2 (i1 b)
outExpr (T op l) = i2 (i2 (op, l))

recExpr :: (a3 -> b3) -> Either b1 (Either b2 (b4, [a3])) -> Either b1 (Either b2 (b4, [b3]))
recExpr = baseExpr id id
\end{code}

Explicação :
\begin{itemize}
\item A função $\mathtt{outExpr}$ implementa o observador do tipo \texttt{Expr}, realizando a desconstrução da estrutura em um tipo soma \texttt{Either}:
  \begin{itemize}
    \item Para variáveis (V a), retorna Left a
    \item Para números (N b), retorna Right (Left b)
    \item Para termos (T op l), retorna Right (Right (op, l))
  \end{itemize}
\item A função $\mathtt{recExpr}$ implementa a recursividade do tipo utilizando \texttt{baseExpr}, mantendo a estrutura original através de funções identidade.
\end{itemize}

\emph{Ana + cata + hylo}:
\begin{code}
cataExpr :: (Either b1 (Either b2 (Op, [b3])) -> b3) -> Expr b2 b1 -> b3
cataExpr g = g . recExpr (cataExpr g) . outExpr

anaExpr :: (a3 -> Either a (Either b (Op, [a3]))) -> a3 -> Expr b a
anaExpr g = inExpr . recExpr (anaExpr g) . g
                
hyloExpr :: (Either b1 (Either b2 (Op, [c])) -> c) -> (a -> Either b1 (Either b2 (Op, [a]))) -> a -> c
hyloExpr h g = cataExpr h . anaExpr g
\end{code}

Através da biblioteca $\mathtt{LTree.hs}$ e do seu catamorfismo, anamorfismo e hilomorfismo implementados,chegamos à construção das funções: 
\begin{itemize}
\item $\mathtt{cataExpr}$: Implementa o catamorfismo que percorre a estrutura recursivamente de baixo para cima:
  \begin{itemize}
    \item Desconstrói a expressão usando $\mathtt{outExpr}$
    \item Aplica recursivamente a transformação usando $\mathtt{recExpr}$
    \item Finaliza aplicando a função de redução $\mathtt{g}$
  \end{itemize}
\item $\mathtt{anaExpr}$: Implementa o anamorfismo que constrói a estrutura recursivamente:
  \begin{itemize}
    \item Usa a função gene $\mathtt{g}$ para criar a estrutura
    \item Aplica recursivamente $\mathtt{recExpr}$
    \item Constrói a expressão final usando $\mathtt{inExpr}$
  \end{itemize}
\item $\mathtt{hyloExpr}$: Combina catamorfismo e anamorfismo em uma única transformação.
\end{itemize}

\subsection*{Resposta à Pergunta 2}

Para declarar \texttt{Expr b} como instância da classe \texttt{Monad}, precisamos primeiro garantir que ela também seja instância de \texttt{Functor} e \texttt{Applicative}, já que estas são superclasses de \texttt{Monad} em Haskell.

\subsubsection*{Functor}
A implementação do \texttt{Functor} é feita usando um catamorfismo:
\begin{code}
instance Functor (Expr b) where
    fmap f = cataExpr (inExpr . baseExpr f id id)
\end{code}
Este \texttt{fmap} aplica a função \texttt{f} apenas às variáveis construtores (\texttt{V}), mantendo constantes (\texttt{N}) e operações (\texttt{T}) inalteradas.

\subsubsection*{Applicative}
Para \texttt{Applicative}, aproveitamos as definições monádicas:
\begin{code}
instance Applicative (Expr b) where
    pure = return
    (<*>) = aap
\end{code}
Onde \texttt{aap} é a função auxiliar que converte operações monádicas em aplicativas.

\subsubsection*{Monad}
A implementação da instância \texttt{Monad} é a parte central:
\begin{code}
instance Monad (Expr b) where
    return = V
    (V a) >>= f = f a
    (N b) >>= f = N b
    (T op l) >>= f = T op (map (>>= f) l)
\end{code}

Vamos analisar cada componente:

\begin{itemize}
    \item \texttt{return = V}: O construtor \texttt{V} serve como a função \texttt{return}, injetando valores puros na expressão
    
    \item \texttt{(V a) >>= f}: Para variáveis, aplicamos a função \texttt{f} diretamente ao valor contido
    
    \item \texttt{(N b) >>= f}: Constantes numéricas permanecem inalteradas
    
    \item \texttt{(T op l) >>= f}: Para operações, mantemos o operador e aplicamos a transformação recursivamente a cada subexpressão na lista
\end{itemize}

Esta implementação satisfaz as leis monádicas:
\begin{enumerate}
    \item Left : \texttt{return a >>= f} = \texttt{f a}
    \item Right : \texttt{m >>= return} = \texttt{m}
    \item Associatividade : \texttt{(m >>= f) >>= g} = \texttt{m >>= (\\x -> f x >>= g)}
\end{enumerate}

A instância \texttt{Monad} permite usar expressões em contextos monádicos, facilitando operações como substituição de variáveis e avaliação de expressões em diferentes contextos.

\subsection*{Resposta à Pergunta 3}

\emph{Maps}:
\emph{Monad}:
\emph{Let expressions}:
\begin{code}
let_exp f = (>>= f)
\end{code}

Esta implementação aproveita a instância \texttt{Monad} de \texttt{Expr}, onde:
\begin{itemize}
  \item O operador \texttt{>>=} (bind) aplica a função de substituição \texttt{f} às variáveis
  \item Para números, a estrutura é mantida enquanto a substituição é aplicada recursivamente
  \item A instância \texttt{Monad} garante que a substituição é feita de forma consistente em toda a expressão
\end{itemize}


\subsection*{Resposta à Pergunta 4}
Catamorfismo monádico:
\begin{code}
mcataExpr f (V a) = f (i1 a)
mcataExpr f (N b) = f (i2 (i1 b))
mcataExpr f (T op l) = f (i2 (i2 (op, mapM (mcataExpr f) l)))
\end{code}
O catamorfismo monádico $\mathtt{mcataExpr}$ é uma versão do catamorfismo adaptada para um contexto monádico. Ele permite transformar um valor de um tipo de dado da estrutura Expr em um tipo de dado monádico.

É uma função recursiva que aplica uma função f a cada elemento, através de um comportamento monádico.
\begin{itemize}
  \item Para variáveis (V a): a função f é aplicada diretamente à variável a, encapsulado em i1
  \item Para números (N b): a função f é aplicada ao número b, encapsulado em i2 . i1
  \item Para termos (T op l):
  \begin{itemize}
       \item Usa mapM para aplicar mcataExpr f recursivamente a cada elemento da lista
       \item Combina o operador e a lista processada em um tuplo
       \item Encapsula em i2 . i2 e aplica f
   \end{itemize}
\end{itemize}


\subsection*{Resposta à Pergunta 5}
Avaliação de expressões:
\begin{code}
evaluate (V _) = Nothing
evaluate (N b) = Just b
evaluate (T Add [x, y]) = (+) <$> evaluate x <*> evaluate y
evaluate (T Mul [x, y]) = (*) <$> evaluate x <*> evaluate y
evaluate (T ITE [cond, thenExpr, elseExpr]) =
    case evaluate cond of
      Just 0 -> evaluate elseExpr
      Just _ -> evaluate thenExpr
      Nothing -> Nothing
evaluate _ = Nothing
\end{code}
A função $\mathtt{evaluate}$ avalia uma expressão do tipo Expr e retorna um valor do tipo \texttt{Maybe a} onde :
\begin{itemize}
  \item \texttt{Nothing} indica que a expressão não é váilida (falhou)
  \item \texttt{Just a} contém o resultado da expressão caso este seja válida
\end{itemize}

A função avalia diferentes padrões de $\mathtt{Expr}$ de acordo com os seguintes casos:

\begin{itemize}
    \item \textbf{Caso 1: Variáveis (\texttt{V})}
    \begin{itemize}
        \item \texttt{evaluate (V \_)} retorna \texttt{Nothing}
        \item Este caso cobre expressões contendo variáveis que não podem ser avaliadas diretamente
    \end{itemize}

    \item \textbf{Caso 2: Valores Numéricos (\texttt{N})}
    \begin{itemize}
        \item \texttt{evaluate (N b)} retorna \texttt{Just b}
        \item Um valor numérico (ou constante) pode ser avaliado diretamente
    \end{itemize}

    \item \textbf{Caso 3: Soma (\texttt{Add})}
    \begin{itemize}
        \item Avalia as duas subexpressões \(x\) e \(y\) e aplica a função soma \(+\) caso ambas sejam válidas
        \item Caso alguma subexpressão não seja válida (\texttt{Nothing}), o resultado será \texttt{Nothing}
    \end{itemize}

    \item \textbf{Caso 4: Multiplicação (\texttt{Mul})}
    \begin{itemize}
        \item Similar ao caso de soma, mas aplica a função multiplicação \(*\)
        \item Exige exatamente duas subexpressões, qualquer número diferente resultará em \texttt{Nothing}
    \end{itemize}

    \item \textbf{Caso 5: Condicional (\texttt{ITE})}
    \begin{itemize}
        \item \texttt{evaluate (T ITE [cond, thenExpr, elseExpr])}
        \item Avalia a condição (\texttt{cond}) primeiro:
        \begin{itemize}
            \item Se a condição for \(0\), avalia e retorna o valor de \texttt{elseExpr}
            \item Se a condição for diferente de \(0\), avalia e retorna o valor de \texttt{thenExpr}
            \item Se a condição for inválida (\texttt{Nothing}), o resultado será \texttt{Nothing}
        \end{itemize}
    \end{itemize}

    \item \textbf{Caso 6: Padrões Genéricos}
    \begin{itemize}
        \item \texttt{evaluate \_ = Nothing}
        \item Este caso cobre situações inválidas, como operadores com número incorreto de subexpressões ou operadores desconhecidos
        \item Por exemplo, \texttt{evaluate (T Add [N 2])} ou \texttt{evaluate (T ITE [N 1, N 2])} retornarão \texttt{Nothing}
    \end{itemize}
\end{itemize}

Com isto ao testar a função com o exemplo do enunciado, concluimos que funcionava corretamente.

%----------------- Índice remissivo (exige makeindex) -------------------------%

\printindex

%----------------- Bibliografia (exige bibtex) --------------------------------%

\bibliographystyle{plain}
\bibliography{cp2425t}

%----------------- Fim do documento -------------------------------------------%
\end{document}
