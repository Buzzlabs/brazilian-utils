# Currency - Formatação de Moeda Brasileira (BRL)

Módulo para formatação e parsing de valores monetários em Real (BRL) com suporte a diferentes precisões decimais e convenções de formatação brasileira.

## Visão Geral

O módulo `currency` fornece utilitários para trabalhar com valores monetários em Real (BRL) brasileiro, com suporte a:
- Formatação de números para padrão BRL (R$ 1.234,56)
- Parsing de strings monetárias para números
- Controle de casas decimais
- Remoção de símbolos e separadores

**Principais capacidades:**
- ✅ Formatação de números para BRL
- ✅ Parsing de strings BRL para números
- ✅ Precisão decimal configurável
- ✅ Suporte a valores negativos
- ✅ Tratamento de valores nulos
- ✅ Compatibilidade com múltiplos tipos numéricos

## Estrutura

O padrão de formatação BRL segue convenções brasileiras:

```
R$ 1.234,56
```

**Componentes:**
- **Símbolo:** R$ (opcional)
- **Separador de milhares:** Ponto (.)
- **Separador decimal:** Vírgula (,)
- **Casas decimais:** Geralmente 2 dígitos

**Exemplos de valores:**
- Inteiro: `R$ 100,00`
- Com milhares: `R$ 1.234,56`
- Negativo: `R$ -1.234,56`
- Sem símbolo: `1.234,56`

## Funções Principais

### `format-currency`

Formata um número como valor em Real (BRL).

**Assinatura:**
```clojure
(format-currency value)
(format-currency value precision)
(format-currency value options)
```

**Argumentos:**
- `value` - Número a formatar (int, float, decimal)
- `precision` - Número de casas decimais (padrão: 2)
- `options` - Mapa com `:precision` (padrão: 2)

**Retorna:** String formatada como BRL (ex: "1.234,56")

**Exemplos:**
```clojure
(format-currency 1234.56)
; => "1.234,56"

(format-currency 100)
; => "100,00"

(format-currency 1234567.89)
; => "1.234.567,89"

(format-currency 1234.567 3)
; => "1.234,567"

(format-currency 1234.567 {:precision 1})
; => "1.234,6"

(format-currency -1234.56)
; => "-1.234,56"

(format-currency 0)
; => "0,00"

(format-currency 0.1)
; => "0,10"
```

### `parse`

Converte uma string formatada em BRL para número decimal.

**Assinatura:**
```clojure
(parse value)
```

**Argumentos:**
- `value` - String monetária em BRL (com ou sem "R$" e separadores)

**Retorna:** Double com o valor numérico

**Exemplos:**
```clojure
(parse "1.234,56")
; => 1234.56

(parse "R$ 1.234,56")
; => 1234.56

(parse "100,00")
; => 100.0

(parse "1234567,89")
; => 1234567.89

(parse "0,01")
; => 0.01

(parse "")
; => 0.0

(parse nil)
; => 0.0

(parse "1000")
; => 1000.0
```

## Recursos

- ✅ Formatação de números para padrão BRL
- ✅ Parsing de strings BRL para números
- ✅ Precisão decimal configurável (0-n casas)
- ✅ Suporte a valores negativos
- ✅ Separação correta de milhares (.) e decimais (,)
- ✅ Tratamento seguro de valores nulos e vazios
- ✅ Compatibilidade com int, float e decimal
- ✅ Sem dependências externas

## Tratamento de Erros

O módulo não lança exceções. Funciona com múltiplos formatos:

```clojure
;; Strings vazias e nulas
(parse "")    ; => 0.0
(parse nil)   ; => 0.0

;; Valores negativos
(format-currency -100.50)  ; => "-100,50"
(parse "-1.234,56")        ; => -1234.56

;; Diferentes formatos
(parse "1234,56")          ; => 1234.56
(parse "1.234,56")         ; => 1234.56
(parse "R$ 1.234,56")      ; => 1234.56
(parse "R$ 1234,56")       ; => 1234.56

;; Números inteiros
(format-currency 100)      ; => "100,00"
(parse "100")              ; => 100.0

;; Casas decimais
(format-currency 1.5)      ; => "1,50"
(parse "0,01")             ; => 0.01
```

## Casos de Uso

### 1. Exibição de Preços em Loja Online

```clojure
(defn display-product-price [price]
  (str "Preço: R$ " (format-currency price)))

(display-product-price 99.90)
; => "Preço: R$ 99,90"

(display-product-price 1500)
; => "Preço: R$ 1.500,00"

(display-product-price 0.01)
; => "Preço: R$ 0,01"
```

### 2. Cálculo de Total de Compra

```clojure
(defn calculate-total [items]
  (let [sum (reduce + (map :price items))
        formatted (format-currency sum)]
    {:items items
     :total-value sum
     :total-formatted (str "R$ " formatted)}))

(calculate-total 
  [{:name "Item 1" :price 50.00}
   {:name "Item 2" :price 30.50}
   {:name "Item 3" :price 19.99}])
; => {:items [...] :total-value 100.49 :total-formatted "R$ 100,49"}
```

### 3. Processamento de Pagamento

```clojure
(defn process-payment [payment-string]
  (let [amount (parse payment-string)]
    (if (> amount 0)
      {:status :valid :amount amount}
      {:status :invalid :message "Valor deve ser maior que zero"})))

(process-payment "R$ 100,50")
; => {:status :valid :amount 100.5}

(process-payment "1.234,56")
; => {:status :valid :amount 1234.56}
```

### 4. Relatório de Vendas

```clojure
(defn generate-sales-report [sales]
  (let [total (reduce + (map :amount sales))
        average (/ total (count sales))
        max-sale (apply max (map :amount sales))
        min-sale (apply min (map :amount sales))]
    {:total-sales (count sales)
     :total-value total
     :total-formatted (format-currency total)
     :average-sale (format-currency average)
     :max-sale (format-currency max-sale)
     :min-sale (format-currency min-sale)}))

(generate-sales-report 
  [{:item "A" :amount 100.50}
   {:item "B" :amount 250.00}
   {:item "C" :amount 49.99}])
; => {:total-sales 3 :total-value 400.49 :total-formatted "400,49" ...}
```

### 5. Conversão de Valor do Usuário

```clojure
(defn process-user-input [input-value]
  (try
    (let [parsed (parse input-value)
          formatted (format-currency parsed)]
      {:status :success 
       :input input-value
       :parsed parsed
       :formatted formatted})
    (catch Exception e
      {:status :error :message (str "Erro ao processar: " (ex-message e))})))

(process-user-input "R$ 1.234,56")
; => {:status :success :input "R$ 1.234,56" :parsed 1234.56 :formatted "1.234,56"}
```

### 6. Precisão Customizável para Diferentes Contextos

```clojure
(defn format-for-context [value context]
  (case context
    :display (format-currency value 2)      ; 2 casas (R$ 100,00)
    :invoice (format-currency value 2)      ; 2 casas (R$ 100,00)
    :accounting (format-currency value 4)   ; 4 casas (R$ 100,0000)
    :crypto (format-currency value 8)       ; 8 casas (R$ 100,00000000)
    (format-currency value)))

(format-for-context 100.5 :display)
; => "100,50"

(format-for-context 100.5 :accounting)
; => "100,5000"

(format-for-context 100.5 :crypto)
; => "100,50000000"
```

## Padrões de Formatação

| Valor | Formatado | Descrição |
|-------|-----------|-----------|
| 0 | 0,00 | Zero |
| 1.5 | 1,50 | Um virgula cinquenta |
| 100 | 100,00 | Cem |
| 1234.56 | 1.234,56 | Mil duzentos e trinta e quatro virgula cinquenta e seis |
| 1000000 | 1.000.000,00 | Um milhão |
| -100 | -100,00 | Negativo |

## Ver Também

- [Email](email.md) - Validação de e-mails
- [CPF](cpf.md) - Validação de CPF
- [CNPJ](cnpj.md) - Validação de CNPJ
- [Capitalize](capitalize.md) - Formatação de texto
