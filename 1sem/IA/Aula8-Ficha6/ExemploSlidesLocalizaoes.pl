localizacao(porto,portugal).
localizacao(lisboa,portugal).
localizacao(coimbra,portugal).
localizacao(caminha,portugal).
localizacao(madrid,espanha).
localizacao(barcelona,espanha).
localizacao(zamora,espanha).
localizacao(orense,espanha).
localizacao(toledo,espanha).
atravessa(douro,porto).
atravessa(douro,zamora).
atravessa(tejo,lisboa).
atravessa(tejo,toledo).
atravessa(minho,caminha).
atravessa(minho,orense).


rio_português(R):-atravessa(R,C),localizacao(C,portugal).
mesmo_rio(C1,C2):-atravessa(R,C1),atravessa(R,C2),C1\==C2.
