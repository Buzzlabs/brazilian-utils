# Boleto - Validação e Análise de Boletos Bancários

Módulo para validação, formatação e análise de boletos bancários brasileiros.

## Visão Geral

Este módulo fornece funções para:

- Validar boletos bancários (47 dígitos)
- Validar boletos de arrecadação (48 dígitos)
- Formatar boleto em padrão de linha digitável
- Extrair e analisar detalhes do boleto
- Identificar banco e calcular vencimento

## Estrutura do Boleto

### Boleto Bancário (47 dígitos)

**Formato Bruto:**
- NNNNNDDPPPPPPPPXXXXXXXXXXXXXXXXXXXXXXVVVVVVVVVVVVV
- Estrutura: Banco + DV + Agência + Conta + Sequencial + DV + Valor

**Formato Linha Digitável:**
- BBBBB.CCCCCC BBBBD.DDDDDD DDDDDD.DDDDDD C VVVVVVVVVVVVV
- Estrutura: [campo1] [campo2] [campo3] [dv] [valor]

### Boleto de Arrecadação (48 dígitos)

- Utilizado para arrecadação de tributos e serviços públicos
- Contém identificação do tipo de arrecadação
- Mesmo algoritmo de validação (módulo 11)

## Algoritmo de Validação

O boleto utiliza o **algoritmo Módulo 11** para o dígito verificador:

1. Pega os primeiros 4 dígitos (banco) e remove o dígito verificador
2. Multiplica os dígitos restantes por sequência cíclica [2, 3, 4, 5, 6, 7, 8, 9, 2, 3, ...]
3. Soma todos os produtos
4. Calcula o resto da divisão por 11
5. Dígito verificador = 11 - resto (se resultado >= 10, usa 0)
6. Compara com o dígito verificador informado

## Funções Principais

### is-valid?

```clojure
(boleto/is-valid? boleto-string)
```

Valida um boleto bancário ou de arrecadação.

**Argumentos:**
- `boleto-string` (string): Boleto a validar, com ou sem formatação

**Retorna:**
- `true` se válido, `false` caso contrário

**Exemplos:**
```clojure
(boleto/is-valid? "00190500954014481606906809350314337370000000100")  ; => true
(boleto/is-valid? "0019 0.00009 01149.718601 68524.522114 6 75860000102656") ; => true
(boleto/is-valid? "00190.00009 01149.718601 68524.522114 6 75860000102656") ; => true
(boleto/is-valid? "inválido")  ; => false
```

**Notas:**
- Aceita 47 ou 48 dígitos
- Suporta com ou sem formatação
- Remove automaticamente pontos, espaços e hífens

### format-linha-digitavel

```clojure
(boleto/format-linha-digitavel boleto-string)
```

Formata um boleto para o padrão de linha digitável.

**Argumentos:**
- `boleto-string` (string): Boleto a formatar (com ou sem formatação)

**Retorna:**
- String formatada como BBBBB.CCCCCC BBBBD.DDDDDD DDDDDD.DDDDDD C VVVVVVVVVVVVV

**Exemplos:**
```clojure
(boleto/format-linha-digitavel "00190000090114971860168524522114675860000102656")
; => "00190.00009 01149.718601 68524.522114 6 75860000102656"

(boleto/format-linha-digitavel "00190.00009 01149.718601 68524.522114 6 75860000102656")
; => "00190.00009 01149.718601 68524.522114 6 75860000102656"
```

### parse-boleto

```clojure
(boleto/parse-boleto boleto-string)
```

Extraia e analise detalhes de um boleto válido.

**Argumentos:**
- `boleto-string` (string): Boleto a analisar (com ou sem formatação)

**Retorna:**
- Map com detalhes analisados ou nil se inválido:
  ```clojure
  {:bank-code "341"
   :bank-name "Banco Itaú"
   :currency "9"
   :due-date-factor 1031
   :due-date "2000-08-03"
   :value 8000002603
   :barcode "34195103180000026035175123456787123412345600"}
  ```

**Exemplos:**
```clojure
(boleto/parse-boleto "34195.17515 23456.787128 34123.456005 5 10318000002603")
; => {:bank-code "341"
;     :bank-name "Banco Itaú"
;     :due-date "2000-08-03"
;     :value 8000002603}

(boleto/parse-boleto "00190500954014481606906809350314337370000000100")
; => {:bank-code "001"
;     :bank-name "Banco do Brasil"
;     :due-date "1998-02-21"
;     :value 314337370}
```

### `barcode->linha-digitavel`

Converta um código de barras para o formato de linha digitável.

**Parâmetros:**
- `barcode` - String contendo o código de barras

**Retorna:** String de linha digitável formatada

```clojure
(barcode->linha-digitavel "34195103180000026035175123456787123412345600")
; => "34195175152345678712834123456005510318000002603"
```

## Tipos de Boletos Suportados

### remove-symbols

```clojure
(boleto/remove-symbols boleto-string)
```

Remove caracteres de formatação de um boleto.

**Argumentos:**
- `boleto-string` (string): Boleto com formatação

**Retorna:**
- String apenas com dígitos

**Exemplos:**
```clojure
(boleto/remove-symbols "00190.00009 01149.718601 68524.522114 6 75860000102656")
; => "001900000901149718601685245221146758600001026560"
```

## Recursos

- ✅ Validação de boleto bancário (47 dígitos)
- ✅ Validação de boleto de arrecadação (48 dígitos)
- ✅ Formatação automática para linha digitável
- ✅ Parsing de detalhes do boleto
- ✅ Identificação de banco e cálculo de vencimento
- ✅ Suporte a múltiplos formatos (com e sem formatação)
- ✅ Cross-platform (Clojure & ClojureScript)

## Tratamento de Erros

```clojure
(boleto/is-valid? "123")
; => false (muito curto)

(boleto/is-valid? "00190000090114971860168524522114675860000102655")
; => false (dígito verificador inválido)

(boleto/parse-boleto "inválido")
; => nil
```

## Casos de Uso

### Validar e processar pagamento

```clojure
(require '[brazilian-utils.boleto :as boleto])

(defn processar-pagamento [boleto-str]
  (if (boleto/is-valid? boleto-str)
    (let [detalhes (boleto/parse-boleto boleto-str)]
      {:status :válido
       :banco (:bank-name detalhes)
       :valor (/ (:value detalhes) 100)
       :vencimento (:due-date detalhes)
       :linha-digitavel (boleto/format-linha-digitavel boleto-str)})
    {:status :inválido
     :mensagem "Boleto inválido"}))

(processar-pagamento "34195.17515 23456.787128 34123.456005 5 10318000002603")
; => {:status :válido
;     :banco "Banco Itaú"
;     :valor 80000.26
;     :vencimento "2000-08-03"
;     :linha-digitavel "34195.17515 23456.787128 34123.456005 5 10318000002603"}
```

### Extrair informações de boleto

```clojure
(defn exibir-detalhes [boleto-str]
  (when-let [detalhes (boleto/parse-boleto boleto-str)]
    (println (format "Banco: %s" (:bank-name detalhes)))
    (println (format "Vencimento: %s" (:due-date detalhes)))
    (println (format "Valor: R$ %.2f" (/ (:value detalhes) 100)))
    (println (format "Linha Digitável: %s" (boleto/format-linha-digitavel boleto-str)))))

(exibir-detalhes "00190500954014481606906809350314337370000000100")
; Saída:
; Banco: Banco do Brasil
; Vencimento: 1998-02-21
; Valor: R$ 3143373.70
; Linha Digitável: 00190.00009 01149.718601 68524.522114 6 75860000102656
```

### Validar em lote

```clojure
(defn validar-boletos-lote [boleto-list]
  (reduce (fn [acc boleto]
            (assoc acc boleto 
                   {:válido? (boleto/is-valid? boleto)
                    :detalhes (when (boleto/is-valid? boleto)
                                (boleto/parse-boleto boleto))}))
          {}
          boleto-list))

(validar-boletos-lote 
  ["00190500954014481606906809350314337370000000100"
   "34195.17515 23456.787128 34123.456005 5 10318000002603"
   "INVÁLIDO"])
```

### Identificar tipo de boleto

```clojure
(defn identificar-tipo [boleto-str]
  (if (boleto/is-valid? boleto-str)
    (let [terceiro-digito (get boleto-str 2)]
      (case (parse-int (str terceiro-digito))
        0 :boleto-bancario
        1 :arrecadacao-prefeitura
        2 :arrecadacao-servicos
        3 :arrecadacao-emprestimo
        5 :arrecadacao-governo
        6 :arrecadacao-impostos
        7 :arrecadacao-taxas
        8 :arrecadacao-saude
        9 :arrecadacao-educacao
        :desconhecido))
    :inválido))

(identificar-tipo "00190500954014481606906809350314337370000000100")
; => :boleto-bancario

(identificar-tipo "23793.12027 12345.678123 01234.567890 8 12345678901234")
; => :arrecadacao-servicos
```

## Bancos Suportados

A biblioteca suporta validação de mais de 250 bancos brasileiros, incluindo:

- **001** - Banco do Brasil
- **033** - Banco Santander
- **104** - Caixa Econômica Federal
- **237** - Banco Bradesco
- **341** - Banco Itaú
- **745** - Banco Citibank
- **747** - Rabobank
- **761** - Banco Plurinacional
- **766** - Banco BBC Brasil
- **801** - Banco Caixa Geral Brasil
- E muitos mais...


## Notas Técnicas

### Fator de Vencimento

O fator de vencimento é calculado como número de dias desde 07/10/1997:

```
fator 0001 = 08/10/1997
fator 1000 = 10/03/2000
fator 1031 = 03/08/2000
```

### Valor do Boleto

O valor é armazenado em centavos (últimos 8 dígitos):

```
Valor 0000000100 = R$ 1,00
Valor 0000010000 = R$ 100,00
Valor 8000002603 = R$ 80.000,26
```

## Ver Também

- [CEP](cep.md) - Para validação de endereços de cobrança
- [Estados](states.md) - Para informações de estados e cidades
    (boleto/format-linha-digitavel entrada)
    nil))

(validar-e-formatar "00190000090114971860168524522114675860000102656")
; => "00190.00009 01149.718601 68524.522114 6 75860000102656"
```

### Identificar Tipo de Boleto

```clojure
(defn identificar-tipo [boleto-str]
  (if (boleto/valid-boleto? boleto-str)
    (let [terceiro-digito (get boleto-str 2)]
      (case (Integer/parseInt (str terceiro-digito))
        0 "Boleto Bancário"
        1 "Arrecadação - Prefeitura"
        2 "Arrecadação - Serviço"
        3 "Arrecadação - Empréstimo"
        5 "Arrecadação - Governo"
        6 "Arrecadação - Impostos"
        7 "Arrecadação - Taxas"
        8 "Arrecadação - Saúde"
        9 "Arrecadação - Educação"
        "Tipo desconhecido"))
    "Boleto inválido"))

(identificar-tipo "00190500954014481606906809350314337370000000100")
; => "Boleto Bancário"

(identificar-tipo "23793.12027 12345.678123 01234.567890 8 12345678901234")
; => "Arrecadação - Serviço"
```

### Extrair Detalhes do Pagamento

```clojure
(def boleto-str "34195.17515 23456.787128 34123.456005 5 10318000002603")

(when-let [detalhes (boleto/parse-boleto boleto-str)]
  (println (format "Banco: %s" (:bank-name detalhes)))
  (println (format "Vencimento: %s" (:due-date detalhes)))
  (println (format "Valor: R$ %.2f" (/ (:value detalhes) 100))))
; Saída:
; Banco: Banco Itaú
; Vencimento: 2000-08-03
; Valor: R$ 80000.26
```

## Bancos Suportados

- 001 - Banco do Brasil
- 033 - Banco Santander
- 104 - Caixa Econômica Federal
- 237 - Banco Bradesco
- 341 - Banco Itaú
- 747 - Rabobank
- E muitos outros...

## Validação de Dígito Verificador

A biblioteca valida automaticamente o dígito verificador usando:
- **Módulo 11** com sequência 2,3,4,5,6,7,8,9,2,3,4,5,6,7,8,9...
- Resto 0 = 0, Resto 1 = 0, demais = 11 - resto

## Módulos Relacionados

- [CEP](cep.md) - Valide códigos de endereçamento postal
- [Estados](states.md) - Obtenha informações de estados
