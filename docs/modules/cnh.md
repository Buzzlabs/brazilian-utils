# CNH - Carteira Nacional de Habilitação

Módulo para validação de CNH (Carteira Nacional de Habilitação), incluindo verificação de dígitos verificadores e remoção de símbolos.

## Visão Geral

O módulo `cnh` fornece validação completa da Carteira Nacional de Habilitação brasileira (CNH), com suporte a:
- Validação de estrutura e dígitos verificadores
- Remoção de caracteres de formatação
- Suporte a formatos com e sem formatação
- Verificação de padrões de CNH válidos

**Principais capacidades:**
- ✅ Validação de CNH com algoritmo de dígitos verificadores
- ✅ Suporte a CNH formatada e não formatada
- ✅ Remoção de caracteres especiais
- ✅ Tratamento de valores nulos
- ✅ Validação de 11 dígitos

## Estrutura

A CNH é um número de identificação para condutores de veículos no Brasil com 11 dígitos:

```
XXXXXXXXXXX
```

**Formato com formatação:**
```
XXXXX.XXXX.XXXX
```

**Características:**
- Total de 11 dígitos
- Contém 2 dígitos verificadores
- Pode incluir pontos para formatação
- Emitida por órgão estadual
- Renovação recomendada a cada 10 anos

## Algoritmo de Validação

A CNH utiliza um algoritmo de validação baseado em módulo:

1. **Primeiro dígito verificador**: Multiplicação dos 9 primeiros dígitos por sequência (9,8,7,6,5,4,3,2,9)
2. **Segundo dígito verificador**: Multiplicação dos próximos 2 dígitos + primeiro verificador

## Funções Principais

### `is-valid?`

Valida se uma CNH é válida segundo o algoritmo oficial.

**Assinatura:**
```clojure
(is-valid? cnh)
```

**Argumentos:**
- `cnh` - String com CNH (formatada ou não)

**Retorna:** Boolean - `true` se for válida, `false` caso contrário

**Exemplos:**
```clojure
(is-valid? "00000000191")    ; => true/false (depende de dígito verificador)
(is-valid? "0000000019")     ; => false (10 dígitos)
(is-valid? "00000000191111") ; => false (12 dígitos)
(is-valid? nil)              ; => false
(is-valid? "")               ; => false
(is-valid? "ABCDEFGHIJK")    ; => false (caracteres não numéricos)
```

### `remove-symbols`

Remove todos os caracteres não numéricos de uma CNH.

**Assinatura:**
```clojure
(remove-symbols cnh)
```

**Argumentos:**
- `cnh` - String com CNH (formatada ou não); nil permitido

**Retorna:** String contendo apenas dígitos

**Exemplos:**
```clojure
(remove-symbols "00000000191")       ; => "00000000191"
(remove-symbols "0000.0000.191")     ; => "00000000191"
(remove-symbols "0000-0000-191")     ; => "00000000191"
(remove-symbols nil)                 ; => ""
(remove-symbols "")                  ; => ""
```

## Recursos

- ✅ Validação completa de CNH com dígitos verificadores
- ✅ Suporte a formatos diversos (com e sem pontos/hífens)
- ✅ Remoção eficiente de caracteres de formatação
- ✅ Tratamento seguro de valores nulos e vazios
- ✅ Validação de número de dígitos
- ✅ Compatível com CNH antigas e novas

## Tratamento de Erros

O módulo não lança exceções. Retorna `false` para entradas inválidas:

```clojure
;; Formato inválido
(is-valid? "ABCDEFGHIJK")  ; => false
(is-valid? "123")          ; => false

;; Valores nulos/vazios
(is-valid? nil)  ; => false
(is-valid? "")   ; => false

;; Dígitos verificadores inválidos
(is-valid? "00000000000")  ; => false (dependendo do algoritmo)

;; Números formatados corretamente
(is-valid? "1234.5678.901")  ; => true/false (depende de dígito verificador)
```

## Casos de Uso

### 1. Validação em Formulários

```clojure
(defn validate-cnh-field [value]
  (let [cleaned (remove-symbols value)]
    (if (is-valid? cleaned)
      {:valid? true :cnh cleaned}
      {:valid? false :message "CNH inválida"})))

(validate-cnh-field "1234.5678.901")
; => {:valid? true :cnh "12345678901"}

(validate-cnh-field "invalid")
; => {:valid? false :message "CNH inválida"}
```

### 2. Normalização de CNH

```clojure
(defn normalize-cnh [cnh-input]
  (let [cleaned (remove-symbols cnh-input)
        trimmed (clojure.string/trim cleaned)]
    (when (is-valid? trimmed)
      trimmed)))

(normalize-cnh "  1234.5678.901  ")
; => "12345678901"

(normalize-cnh "invalid")
; => nil
```

### 3. Validação em Lote de Condutores

```clojure
(defn validate-drivers [drivers]
  (map (fn [driver]
         (assoc driver 
                :cnh-valid? (is-valid? (remove-symbols (:cnh driver)))))
       drivers))

(validate-drivers 
  [{:name "João" :cnh "1234.5678.901"}
   {:name "Maria" :cnh "invalid"}
   {:name "Pedro" :cnh "9876.5432.109"}])
; => [{:name "João" :cnh-valid? true} ...]
```

### 4. Filtragem de CNHs Válidas

```clojure
(defn filter-valid-cnhs [cnh-list]
  (->> cnh-list
       (map remove-symbols)
       (filter is-valid?)))

(filter-valid-cnhs 
  ["1234.5678.901"
   "invalid"
   "9876.5432.109"
   nil])
; => ["12345678901" "98765432109"]
```

### 5. Integração com Sistema de Cadastro

```clojure
(defn register-driver [driver-info]
  (let [{:keys [name cnh]} driver-info
        clean-cnh (remove-symbols cnh)]
    (if (is-valid? clean-cnh)
      {:status :success 
       :driver {:name name :cnh clean-cnh}}
      {:status :error 
       :message "CNH inválida para registro"})))

(register-driver {:name "João Silva" :cnh "1234.5678.901"})
; => {:status :success :driver {...}}
```

### 6. Comparação de CNHs

```clojure
(defn are-same-cnh? [cnh1 cnh2]
  (let [clean1 (remove-symbols cnh1)
        clean2 (remove-symbols cnh2)]
    (and (is-valid? clean1)
         (is-valid? clean2)
         (= clean1 clean2))))

(are-same-cnh? "1234.5678.901" "12345678901")
; => true

(are-same-cnh? "1234.5678.901" "9876.5432.109")
; => false
```

## Formatos Aceitos

| Formato | Exemplo | Válido |
|---------|---------|--------|
| Sem formatação | 12345678901 | ✅ |
| Com pontos | 1234.5678.901 | ✅ |
| Com hífens | 1234-5678-901 | ✅ |
| Misto | 1234.5678-901 | ✅ |
| Com espaços | 1234 5678 901 | ✅ |

## Ver Também

- [Título Eleitoral](titulo_eleitoral.md) - Validação de Título Eleitoral
- [RENAVAM](renavam.md) - Validação de RENAVAM (veículos)
- [CPF](cpf.md) - Validação de CPF
- [Documentos]() - Validação de documentos em geral
