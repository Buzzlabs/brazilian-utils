# RENAVAM - Registro Nacional de Veículo Automotor

Módulo para validação de RENAVAM (Registro Nacional de Veículo Automotor), incluindo suporte a formatos de 9 e 11 dígitos, com dígito verificador.

## Visão Geral

O módulo `renavam` fornece validação completa do RENAVAM brasileiro, com suporte a:
- Validação de números com 9 ou 11 dígitos
- Verificação de dígito verificador
- Remoção de caracteres de formatação
- Suporte a registros antigos e novos

**Principais capacidades:**
- ✅ Validação de RENAVAM de 9 dígitos (formato antigo)
- ✅ Validação de RENAVAM de 11 dígitos (formato novo)
- ✅ Verificação de dígito verificador
- ✅ Remoção de caracteres especiais
- ✅ Tratamento de múltiplos formatos

## Estrutura

O RENAVAM pode ter dois formatos válidos:

**Formato antigo (9 dígitos):**
```
XXXXXXXXX
```

**Formato novo (11 dígitos):**
```
XXXXXXXXXXX
```

**Formato com formatação (novo):**
```
XX.XXX.XXX-XX
```

**Características:**
- Pode ser de 9 ou 11 dígitos
- Contém dígito verificador (nos 11 dígitos)
- Emitido pelo DENATRAN
- Registra proprietário e características do veículo

## Algoritmo de Validação

A validação RENAVAM utiliza módulo 11:

1. Multiplica cada dígito por sequência específica (9,8,7,6,5,4,3,2)
2. Calcula o módulo 11 da soma
3. Verifica o dígito verificador (11º dígito)

## Funções Principais

### `is-valid?`

Valida se um RENAVAM é válido.

**Assinatura:**
```clojure
(is-valid? renavam)
```

**Argumentos:**
- `renavam` - String com RENAVAM (formatada ou não)

**Retorna:** Boolean - `true` se for válido, `false` caso contrário

**Exemplos:**
```clojure
(is-valid? "12345678901")     ; => true/false (depende de dígito verificador)
(is-valid? "123456789")       ; => true/false (formato antigo)
(is-valid? "12.345.678-90")   ; => true/false
(is-valid? "12345678")        ; => false (8 dígitos)
(is-valid? nil)               ; => false
(is-valid? "")                ; => false
```

### `remove-symbols`

Remove todos os caracteres não numéricos de um RENAVAM.

**Assinatura:**
```clojure
(remove-symbols renavam)
```

**Argumentos:**
- `renavam` - String com RENAVAM (formatada ou não); nil permitido

**Retorna:** String contendo apenas dígitos

**Exemplos:**
```clojure
(remove-symbols "12345678901")    ; => "12345678901"
(remove-symbols "12.345.678-90")  ; => "1234567890"
(remove-symbols "123456789")      ; => "123456789"
(remove-symbols nil)              ; => ""
(remove-symbols "")               ; => ""
```

## Recursos

- ✅ Validação de RENAVAM de 9 dígitos (antigo)
- ✅ Validação de RENAVAM de 11 dígitos (novo)
- ✅ Verificação robusta de dígito verificador
- ✅ Suporte a múltiplos formatos de entrada
- ✅ Remoção eficiente de caracteres de formatação
- ✅ Tratamento seguro de valores nulos e vazios
- ✅ Compatibilidade com registros antigos e novos

## Tratamento de Erros

O módulo não lança exceções. Retorna `false` para entradas inválidas:

```clojure
;; Formato inválido
(is-valid? "ABCDEFGHIJK")  ; => false
(is-valid? "12345678")     ; => false (8 dígitos)

;; Valores nulos/vazios
(is-valid? nil)  ; => false
(is-valid? "")   ; => false

;; Dígitos verificadores inválidos
(is-valid? "00000000000")  ; => false (dependendo do algoritmo)

;; Números formatados
(is-valid? "12.345.678-90")  ; => true/false (depende de dígito verificador)
```

## Casos de Uso

### 1. Validação em Registro de Veículo

```clojure
(defn register-vehicle [vehicle-info]
  (let [{:keys [renavam model]} vehicle-info
        clean-renavam (remove-symbols renavam)]
    (if (is-valid? clean-renavam)
      {:status :success :vehicle {:renavam clean-renavam :model model}}
      {:status :error :message "RENAVAM inválido"})))

(register-vehicle {:renavam "12.345.678-90" :model "Fiat Uno"})
; => {:status :success :vehicle {...}}
```

### 2. Normalização de RENAVAM

```clojure
(defn normalize-renavam [renavam-input]
  (let [cleaned (remove-symbols renavam-input)
        trimmed (clojure.string/trim cleaned)]
    (when (is-valid? trimmed)
      trimmed)))

(normalize-renavam "  12.345.678-90  ")
; => "12345678901"

(normalize-renavam "invalid")
; => nil
```

### 3. Validação em Lote de Veículos

```clojure
(defn validate-vehicles [vehicles]
  (map (fn [vehicle]
         (assoc vehicle 
                :renavam-valid? (is-valid? (remove-symbols (:renavam vehicle)))))
       vehicles))

(validate-vehicles 
  [{:plate "ABC-1234" :renavam "12.345.678-90"}
   {:plate "XYZ-5678" :renavam "invalid"}
   {:plate "DEF-9012" :renavam "98765432109"}])
; => [{:renavam-valid? true} ...]
```

### 4. Migração de Sistema Antigo para Novo

```clojure
(defn upgrade-renavam [old-renavam]
  (let [cleaned (remove-symbols old-renavam)]
    (cond
      (and (is-valid? cleaned)
           (= 11 (count cleaned)))
      {:format :new :renavam cleaned}
      
      (and (is-valid? cleaned)
           (= 9 (count cleaned)))
      {:format :old :renavam cleaned :needs-update true}
      
      :else
      {:format :invalid :renavam cleaned})))

(upgrade-renavam "123456789")
; => {:format :old :renavam "123456789" :needs-update true}

(upgrade-renavam "12.345.678-90")
; => {:format :new :renavam "12345678901"}
```

### 5. Filtragem de RENAVAMs Válidos em Lote

```clojure
(defn filter-valid-renavams [renavam-list]
  (->> renavam-list
       (map remove-symbols)
       (filter is-valid?)))

(filter-valid-renavams 
  ["12.345.678-90"
   "invalid"
   "98765432109"
   "123456789"
   nil])
; => ["12345678901" "98765432109" "123456789"]
```

### 6. Integração com Banco de Dados

```clojure
(defn save-vehicle-record [db vehicle]
  (let [renavam (remove-symbols (:renavam vehicle))]
    (if (is-valid? renavam)
      (do
        (db/save db {:renavam renavam 
                     :owner (:owner vehicle)
                     :model (:model vehicle)})
        {:status :saved})
      {:status :error :message "RENAVAM inválido"})))

(save-vehicle-record my-db 
  {:renavam "12.345.678-90" 
   :owner "João Silva" 
   :model "Toyota Corolla"})
; => {:status :saved}
```

## Formatos Aceitos

| Formato | Exemplo | Válido |
|---------|---------|--------|
| 9 dígitos (antigo) | 123456789 | ✅ |
| 11 dígitos (novo) | 12345678901 | ✅ |
| Formatado | 12.345.678-90 | ✅ |
| Com espaços | 12 345 678 90 | ✅ |
| 8 dígitos | 12345678 | ❌ |
| 10 dígitos | 1234567890 | ❌ |

## Histórico de Formato

| Período | Formato | Dígitos | Notas |
|---------|---------|---------|-------|
| Antigo | XXXXXXXXX | 9 | Ainda válido, sem dígito verificador |
| Novo | XX.XXX.XXX-XX | 11 | Formato atual, com dígito verificador |

## Ver Também

- [CNH](cnh.md) - Carteira Nacional de Habilitação
- [Título Eleitoral](titulo_eleitoral.md) - Validação de Título Eleitoral
- [CPF](cpf.md) - Validação de CPF
- [CNPJ](cnpj.md) - Validação de CNPJ
