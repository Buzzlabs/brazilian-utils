# Inscrição Estadual - Validação de IE por Estado

Módulo para validação de Inscrição Estadual (IE) com suporte aos 27 estados brasileiros, cada um com seus próprios algoritmos de validação e formatos.

## Visão Geral

O módulo `inscricao-estadual` fornece validação completa de números de Inscrição Estadual (IE) brasileiros, com suporte a:
- Validação específica por estado
- Algoritmos diferentes para cada estado
- Múltiplos tamanhos de IE (alguns estados aceitam 8, 9, 12 ou 13 dígitos)
- Remoção de caracteres de formatação

**Principais capacidades:**
- ✅ Validação de IE para cada um dos 27 estados
- ✅ Suporte a múltiplos formatos por estado
- ✅ Algoritmos específicos de validação
- ✅ Remoção de caracteres de formatação
- ✅ Compatibilidade com formatos antigos e novos

## Estrutura

A IE varia em tamanho e algoritmo de acordo com o estado:

**Tamanhos comuns:**
- 8 dígitos: Bahia
- 9 dígitos: Alagoas, Amapá, Amazonas, Ceará, Distrito Federal, Espírito Santo, Goiás, Maranhão, Mato Grosso do Sul, Pará, Paraíba, Pernambuco, Piauí, Rio Grande do Norte, Rondônia, Roraima, Sergipe
- 10 dígitos: Goiás (novo formato)
- 12 dígitos: Minas Gerais, São Paulo
- 13 dígitos: Acre, Distrito Federal (alternativo), Espírito Santo (alternativo), Mato Grosso, Pará (alternativo), Rondônia (alternativo)

**Formato com formatação (exemplo SP):**
```
111.222.333.444
```

## Funções Principais

### `is-valid?`

Valida se uma Inscrição Estadual é válida para um determinado estado.

**Assinatura:**
```clojure
(is-valid? uf ie)
```

**Argumentos:**
- `uf` - Keyword ou string do estado (ex: `:SP`, `"SP"`, `:RJ`)
- `ie` - String com a IE (formatada ou não)

**Retorna:** Boolean - `true` se for válida, `false` caso contrário

**Exemplos:**
```clojure
(is-valid? :SP "110042490114")       ; => true/false (depende de dígito verificador)
(is-valid? :SP "11.004.249.0114")    ; => true/false (com formatação)
(is-valid? :RJ "12345678901")        ; => true/false
(is-valid? :BA "12345678")           ; => true/false (8 dígitos)
(is-valid? :MG "123456789012")       ; => true/false (12 dígitos)
(is-valid? :XX "123456")             ; => false (estado inválido)
(is-valid? :SP nil)                  ; => false
(is-valid? :SP "")                   ; => false
```

### `remove-symbols`

Remove todos os caracteres não numéricos de uma Inscrição Estadual.

**Assinatura:**
```clojure
(remove-symbols ie)
```

**Argumentos:**
- `ie` - String com a IE (formatada ou não); nil permitido

**Retorna:** String contendo apenas dígitos

**Exemplos:**
```clojure
(remove-symbols "11.004.249.0114")  ; => "110042490114"
(remove-symbols "110042490114")     ; => "110042490114"
(remove-symbols "11 004 249 0114")  ; => "110042490114"
(remove-symbols "11-004-249-0114")  ; => "110042490114"
(remove-symbols nil)                ; => ""
(remove-symbols "")                 ; => ""
```

## Recursos

- ✅ Validação de IE para todos os 27 estados brasileiros
- ✅ Suporte a múltiplos formatos de IE por estado
- ✅ Algoritmos específicos e precisos por estado
- ✅ Remoção eficiente de caracteres de formatação
- ✅ Tratamento seguro de valores nulos e vazios
- ✅ Case-insensitive para códigos de estado
- ✅ Compatibilidade com IE antigas e novas

## Tratamento de Erros

O módulo não lança exceções. Retorna `false` para entradas inválidas:

```clojure
;; Estado inválido
(is-valid? :XX "123456")  ; => false

;; Formato incorreto para estado
(is-valid? :SP "12345678")        ; => false (SP precisa de 12 dígitos)
(is-valid? :BA "123456789")       ; => false (BA precisa de 8 dígitos)

;; Valores nulos/vazios
(is-valid? :SP nil)  ; => false
(is-valid? :SP "")   ; => false

;; Dígitos verificadores inválidos
(is-valid? :SP "000000000000")  ; => false (verificador incorreto)
```

## Casos de Uso

### 1. Validação em Cadastro de Empresa

```clojure
(defn register-company [company-info]
  (let [{:keys [name uf ie]} company-info
        clean-ie (remove-symbols ie)
        uf-key (keyword (clojure.string/upper-case uf))]
    (if (is-valid? uf-key clean-ie)
      {:status :success :company {:name name :uf uf-key :ie clean-ie}}
      {:status :error :message "IE inválida para este estado"})))

(register-company {:name "Empresa XYZ" :uf "SP" :ie "11.004.249.0114"})
; => {:status :success :company {...}}
```

### 2. Validação de IE com Lookup de Estado

```clojure
(defn validate-ie-with-state [company-data]
  (let [{:keys [name ie state]} company-data
        clean-ie (remove-symbols ie)
        uf-key (keyword (clojure.string/upper-case state))]
    {:company name
     :state uf-key
     :ie clean-ie
     :valid? (is-valid? uf-key clean-ie)
     :message (if (is-valid? uf-key clean-ie)
                "IE válida"
                "IE inválida para este estado")}))

(validate-ie-with-state 
  {:name "Empresa A" :ie "11.004.249.0114" :state "SP"})
; => {:company "Empresa A" :state :SP :ie "110042490114" :valid? true :message "IE válida"}
```

### 3. Validação em Lote de Empresas

```clojure
(defn validate-companies [companies]
  (map (fn [company]
         (let [clean-ie (remove-symbols (:ie company))
               uf-key (keyword (clojure.string/upper-case (:uf company)))]
           (assoc company 
                  :ie-valid? (is-valid? uf-key clean-ie)
                  :ie-normalized clean-ie)))
       companies))

(validate-companies 
  [{:name "Empresa A" :uf "SP" :ie "11.004.249.0114"}
   {:name "Empresa B" :uf "RJ" :ie "12345678901"}
   {:name "Empresa C" :uf "MG" :ie "123456789012"}])
; => [{:ie-valid? true ...} {:ie-valid? true ...} ...]
```

### 4. Processamento de Nota Fiscal

```clojure
(defn validate-nfe-emitter [nfe-data]
  (let [{:keys [emitter-uf emitter-ie]} nfe-data
        clean-ie (remove-symbols emitter-ie)
        uf-key (keyword (clojure.string/upper-case emitter-uf))]
    (if (is-valid? uf-key clean-ie)
      {:status :valid :emitter-ie clean-ie}
      {:status :invalid 
       :message "IE do emissor inválida para processamento de NF-e"})))

(validate-nfe-emitter 
  {:emitter-uf "SP" :emitter-ie "11.004.249.0114"})
; => {:status :valid :emitter-ie "110042490114"}
```

### 5. Normalização de IE para Banco de Dados

```clojure
(defn normalize-ie [uf ie]
  (let [clean (remove-symbols ie)
        uf-key (keyword (clojure.string/upper-case uf))]
    (when (is-valid? uf-key clean)
      clean)))

(normalize-ie "SP" "11.004.249.0114")
; => "110042490114"

(normalize-ie "RJ" "invalid")
; => nil
```

### 6. Integração com Sistema de Impostos

```clojure
(defn submit-tax-info [taxpayer-info]
  (let [{:keys [uf ie company-name]} taxpayer-info
        clean-ie (remove-symbols ie)
        uf-key (keyword (clojure.string/upper-case uf))]
    (if (is-valid? uf-key clean-ie)
      {:status :submitted
       :taxpayer {:name company-name
                  :uf uf-key
                  :ie clean-ie
                  :timestamp (java.time.Instant/now)}}
      {:status :error
       :message "IE inválida - não foi possível submeter ao sistema de impostos"})))

(submit-tax-info 
  {:uf "SP" :ie "11.004.249.0114" :company-name "Empresa XYZ"})
; => {:status :submitted :taxpayer {...}}
```

## Tamanhos de IE por Estado

| UF | Tamanho(s) | Exemplos | Algoritmo |
|----|-----------|----------|-----------|
| AC | 13 | 0160050111000 | Módulo 11 |
| AL | 9 | 170.765.490 | Módulo 11 |
| AP | 9 | 160.000.000 | Módulo 11 |
| AM | 9 | 160.000.000 | Módulo 11 |
| BA | 8 ou 9 | 16.000.000 | Módulo 11 |
| CE | 9 | 160.000.000 | Módulo 11 |
| DF | 13 | 0760001980251 | Módulo 11 |
| ES | 13 | 0760001980251 | Módulo 11 |
| GO | 10 ou 14 | 1.234.567.001-97 | Módulo 11 |
| MA | 9 | 160.000.000 | Módulo 11 |
| MG | 12 | 160.000.000/0001 | Módulo 11 |
| MT | 13 | 0160050111000 | Módulo 11 |
| MS | 9 | 160.000.000 | Módulo 11 |
| PA | 13 | 0760001980251 | Módulo 11 |
| PB | 9 | 160.000.000 | Módulo 11 |
| PE | 8 ou 9 | 16.000.000 | Módulo 11 |
| PI | 9 | 160.000.000 | Módulo 11 |
| PR | 10 ou 11 | 170.765.490 | Módulo 11 |
| RJ | 8 | 12345678 | Módulo 11 |
| RN | 9 ou 10 | 20.000.000 | Módulo 11 |
| RO | 14 | 00.000.000.000 | Módulo 11 |
| RS | 10 | 160.000.000.000 | Módulo 11 |
| RR | 9 | 160.000.000 | Módulo 11 |
| SC | 10 ou 11 | 160.000.000.000 | Módulo 11 |
| SE | 9 | 160.000.000 | Módulo 11 |
| SP | 12 | 110.042.490.114 | Módulo 11 |
| TO | 13 | 0760001980251 | Módulo 11 |

## Ver Também

- [Estados](states.md) - Informações de estados brasileiros
- [CPF](cpf.md) - Validação de CPF
- [CNPJ](cnpj.md) - Validação de CNPJ
- [PIS](pis.md) - Validação de PIS
- [Título Eleitoral](titulo_eleitoral.md) - Validação de Título Eleitoral
