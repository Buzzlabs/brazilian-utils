# Estados - Informações de Estados Brasileiros

Módulo para validação, recuperação e manipulação de informações sobre os 27 estados brasileiros (UF - Unidade Federativa), incluindo nomes completos, códigos de área (DDDs) e comprimentos de Inscrição Estadual (IE).

## Visão Geral

O módulo `states` fornece um conjunto completo de funções para trabalhar com os estados brasileiros. Permite validar códigos de estado, recuperar nomes completos, obter códigos de telefone (DDDs), determinar o tamanho esperado da Inscrição Estadual (IE) e listar todos os estados disponíveis.

**Principais capacidades:**
- ✅ Validação de códigos de estado (UF)
- ✅ Conversão UF ↔ Nome completo
- ✅ Recuperação de DDDs (códigos de área)
- ✅ Informações de Inscrição Estadual (IE)
- ✅ Listagem completa de estados
- ✅ Busca bidirecional (código ↔ nome)

## Estrutura

O módulo trabalha com os 27 estados brasileiros, cada um identificado por um código de 2 letras (UF). Os estados estão organizados por região geográfica:

**Regiões:**
- **Norte:** AC, AP, AM, PA, RO, RR, TO (7 estados)
- **Nordeste:** AL, BA, CE, MA, PB, PE, PI, RN, SE (9 estados)
- **Centro-Oeste:** DF, GO, MT, MS (4 estados)
- **Sudeste:** ES, MG, RJ, SP (4 estados)
- **Sul:** PR, RS, SC (3 estados)

Cada estado possui associações com:
- Nome completo em português
- Códigos de área de telefone (DDDs)
- Tamanho padrão de Inscrição Estadual (IE)
- Região geográfica

## Funções Principais

### `valid-uf?`

Valida se um valor é um código de estado brasileiro válido.

**Assinatura:**
```clojure
(valid-uf? uf)
```

**Argumentos:**
- `uf` - Keyword ou string representando a abreviação do estado (case-insensitive)

**Retorna:** Boolean - `true` se for um código válido, `false` caso contrário

**Exemplos:**
```clojure
(valid-uf? :SP)     ; => true
(valid-uf? :RJ)     ; => true
(valid-uf? :XX)     ; => false
(valid-uf? "SP")    ; => true
(valid-uf? "sp")    ; => true
(valid-uf? :ZZ)     ; => false
```

### `uf->state-name`

Obtém o nome completo do estado para um código de UF.

**Assinatura:**
```clojure
(uf->state-name uf)
```

**Argumentos:**
- `uf` - Keyword ou string representando a abreviação do estado (case-insensitive)

**Retorna:** String com o nome completo do estado ou `nil` se inválido

**Exemplos:**
```clojure
(uf->state-name :SP)      ; => "São Paulo"
(uf->state-name :RJ)      ; => "Rio de Janeiro"
(uf->state-name :BA)      ; => "Bahia"
(uf->state-name "minas")  ; => nil
(uf->state-name :MG)      ; => "Minas Gerais"
(uf->state-name :XX)      ; => nil
```

### `uf->area-codes`

Obtém todos os códigos de área de telefone (DDDs) para um estado.

**Assinatura:**
```clojure
(uf->area-codes uf)
```

**Argumentos:**
- `uf` - Keyword ou string representando a abreviação do estado (case-insensitive)

**Retorna:** Vetor de inteiros com os DDDs do estado ou `nil` se inválido

**Exemplos:**
```clojure
(uf->area-codes :SP)  ; => [11 12 13 14 15 16 17 18 19]
(uf->area-codes :RJ)  ; => [21 22 24]
(uf->area-codes :BA)  ; => [71 73 74 75 77]
(uf->area-codes :DF)  ; => [61]
(uf->area-codes :XX)  ; => nil
```

### `uf->ie-length`

Obtém o tamanho esperado da Inscrição Estadual (IE) para um estado.

**Assinatura:**
```clojure
(uf->ie-length uf)
```

**Argumentos:**
- `uf` - Keyword ou string representando a abreviação do estado (case-insensitive)

**Retorna:** Inteiro (tamanho único) ou vetor de inteiros (múltiplos tamanhos aceitos) ou `nil` se inválido

**Exemplos:**
```clojure
(uf->ie-length :SP)  ; => 12
(uf->ie-length :MG)  ; => 12
(uf->ie-length :BA)  ; => [8 9]
(uf->ie-length :AC)  ; => 13
(uf->ie-length :XX)  ; => nil
```

### `all-ufs`

Obtém todos os 27 códigos de estado brasileiros válidos em ordem alfabética.

**Assinatura:**
```clojure
(all-ufs)
```

**Argumentos:** Nenhum

**Retorna:** Vetor de keywords com todos os códigos de estado

**Exemplos:**
```clojure
(all-ufs)
; => [:AC :AL :AP :AM :BA :CE :DF :ES :GO :MA :MG :MT :MS 
;     :PA :PB :PE :PI :PR :RJ :RN :RO :RS :RR :SC :SE :SP :TO]

(count (all-ufs))  ; => 27
```

### `all-state-names`

Obtém os nomes completos de todos os 27 estados brasileiros em ordem alfabética.

**Assinatura:**
```clojure
(all-state-names)
```

**Argumentos:** Nenhum

**Retorna:** Vetor de strings com os nomes dos estados

**Exemplos:**
```clojure
(all-state-names)
; => ["Acre" "Alagoas" "Amapá" "Amazonas" "Bahia" "Ceará" "Distrito Federal"
;     "Espírito Santo" "Goiás" "Maranhão" "Minas Gerais" "Mato Grosso"
;     "Mato Grosso do Sul" "Pará" "Paraíba" "Pernambuco" "Piauí" "Paraná"
;     "Rio de Janeiro" "Rio Grande do Norte" "Rondônia" "Rio Grande do Sul"
;     "Roraima" "Santa Catarina" "Sergipe" "São Paulo" "Tocantins"]

(first (all-state-names))  ; => "Acre"
```

## Recursos

- ✅ Validação robusta de códigos de estado (UF)
- ✅ Conversão bidirecional entre códigos e nomes
- ✅ Acesso a informações de DDDs por estado
- ✅ Informações sobre tamanho de Inscrição Estadual (IE)
- ✅ Suporte a múltiplos formatos de entrada (keyword, string, case-insensitive)
- ✅ Listagem completa e ordenada de estados
- ✅ Dados precisos dos 27 estados brasileiros

## Tratamento de Erros

O módulo não lança exceções. Todas as funções de busca retornam `nil` para códigos inválidos:

```clojure
;; Validação segura
(if (valid-uf? user-input)
  (uf->state-name user-input)
  "Estado inválido")

;; Ou usar a abordagem nil-safe
(or (uf->state-name input) "Desconhecido")

;; Tratamento com some
(some-> user-input
        keyword
        (cond-> (valid-uf? %) (uf->state-name %)))

;; Com default values
(get {"SP" "São Paulo" "RJ" "Rio de Janeiro"} input "Estado desconhecido")
```

## Casos de Uso

### 1. Validação de Estado em Formulários

```clojure
(defn validate-state-field [value]
  (let [state (-> value string/trim string/upper-case keyword)]
    (if (valid-uf? state)
      {:valid? true :state state}
      {:valid? false :message "Estado inválido"})))

(validate-state-field "sp")       ; => {:valid? true :state :SP}
(validate-state-field "  RJ  ")   ; => {:valid? true :state :RJ}
(validate-state-field "XX")       ; => {:valid? false :message "Estado inválido"}
```

### 2. Filtragem de DDDs por Múltiplos Estados

```clojure
(defn get-all-area-codes-for-states [ufs]
  (->> ufs
       (map uf->area-codes)
       (remove nil?)
       (mapcat identity)
       distinct
       sort))

(get-all-area-codes-for-states [:SP :RJ :MG])
; => [11 12 13 14 15 16 17 18 19 21 22 24 31 32 33 34 35 37 38]

(count (get-all-area-codes-for-states [:SP :RJ :MG]))
; => 19 DDDs únicos
```

### 3. Lookup Reverso - Estado por DDD

```clojure
(defn find-states-by-ddd [ddd]
  (filter #(some #{ddd} (uf->area-codes %)) (all-ufs)))

(find-states-by-ddd 11)   ; => [:SP]
(find-states-by-ddd 21)   ; => [:RJ]
(find-states-by-ddd 85)   ; => [:CE]
(find-states-by-ddd 999)  ; => []
```

### 4. Validação de IE com Tamanho de Estado

```clojure
(defn validate-ie-for-state [uf ie-string]
  (let [uf-key (keyword (string/upper-case uf))
        ie-length (uf->ie-length uf-key)
        ie-size (count ie-string)]
    (cond
      (not (valid-uf? uf-key))
      {:valid? false :error "Estado inválido"}
      
      (nil? ie-length)
      {:valid? false :error "Comprimento de IE desconhecido"}
      
      (vector? ie-length)
      {:valid? (some #{ie-size} ie-length) :allowed-sizes ie-length}
      
      :else
      {:valid? (= ie-size ie-length) :expected-size ie-length})))

(validate-ie-for-state "SP" "123456789012")
; => {:valid? true :expected-size 12}

(validate-ie-for-state "BA" "123456789")
; => {:valid? true :allowed-sizes [8 9]}

(validate-ie-for-state "XX" "123")
; => {:valid? false :error "Estado inválido"}
```

### 5. Comparação de Regiões por Estados

```clojure
(def state-regions
  {:norte   [:AC :AP :AM :PA :RO :RR :TO]
   :nordeste [:AL :BA :CE :MA :PB :PE :PI :RN :SE]
   :centro-oeste [:DF :GO :MT :MS]
   :sudeste [:ES :MG :RJ :SP]
   :sul     [:PR :RS :SC]})

(defn states-in-region [region]
  (get state-regions region []))

(defn get-region-for-state [uf]
  (->> state-regions
       (filter (fn [[_ states]] (some #{uf} states)))
       ffirst))

(states-in-region :sudeste)
; => [:ES :MG :RJ :SP]

(get-region-for-state :SP)
; => :sudeste

(map uf->state-name (states-in-region :nordeste))
; => ("Alagoas" "Bahia" "Ceará" "Maranhão" "Paraíba" 
;     "Pernambuco" "Piauí" "Rio Grande do Norte" "Sergipe")
```

### 6. Relatório de Cobertura Geográfica

```clojure
(defn coverage-by-region []
  (let [regions {:norte   [:AC :AP :AM :PA :RO :RR :TO]
                 :nordeste [:AL :BA :CE :MA :PB :PE :PI :RN :SE]
                 :centro-oeste [:DF :GO :MT :MS]
                 :sudeste [:ES :MG :RJ :SP]
                 :sul     [:PR :RS :SC]}]
    (into {}
          (map (fn [[region states]]
                 [region
                  {:count (count states)
                   :states (map uf->state-name states)
                   :total-ddd (->> states
                                   (mapcat uf->area-codes)
                                   distinct
                                   count)}])
               regions))))

(coverage-by-region)
; => {:norte {:count 7 :states [...] :total-ddd 23}
;     :nordeste {:count 9 :states [...] :total-ddd 19}
;     ...}
```

## Tabela de Estados e Inscrições Estaduais (IE)

| UF | Estado | Região | Tamanho IE | DDDs Principais |
|----|--------|--------|------------|-----------------|
| AC | Acre | Norte | 13 | 68 |
| AL | Alagoas | Nordeste | 9 | 82 |
| AP | Amapá | Norte | 9 | 96 |
| AM | Amazonas | Norte | 9 | 92, 97 |
| BA | Bahia | Nordeste | 8-9 | 71, 73, 74, 75, 77 |
| CE | Ceará | Nordeste | 9 | 85, 88 |
| DF | Distrito Federal | Centro-Oeste | 13 | 61 |
| ES | Espírito Santo | Sudeste | 13 | 27, 28 |
| GO | Goiás | Centro-Oeste | 10 | 62, 64 |
| MA | Maranhão | Nordeste | 9 | 98, 99 |
| MG | Minas Gerais | Sudeste | 12 | 31, 32, 33, 34, 35, 37, 38 |
| MT | Mato Grosso | Centro-Oeste | 13 | 65, 66 |
| MS | Mato Grosso do Sul | Centro-Oeste | 9 | 67 |
| PA | Pará | Norte | 13 | 91, 93, 94 |
| PB | Paraíba | Nordeste | 9 | 83 |
| PE | Pernambuco | Nordeste | 8-9 | 81, 87 |
| PI | Piauí | Nordeste | 9 | 86, 89 |
| PR | Paraná | Sul | 10-11 | 41, 42, 43, 44, 45, 46 |
| RJ | Rio de Janeiro | Sudeste | 8-9 | 21, 22, 24 |
| RN | Rio Grande do Norte | Nordeste | 9-10 | 84 |
| RO | Rondônia | Norte | 14 | 69 |
| RS | Rio Grande do Sul | Sul | 10 | 51, 53, 54, 55 |
| RR | Roraima | Norte | 9 | 95 |
| SC | Santa Catarina | Sul | 10-11 | 47, 48, 49 |
| SE | Sergipe | Nordeste | 9 | 79 |
| SP | São Paulo | Sudeste | 12 | 11, 12, 13, 14, 15, 16, 17, 18, 19 |
| TO | Tocantins | Norte | 13 | 63 |

## Ver Também

- [Cidades](cities.md) - Validação e lookup de cidades por estado
- [CEP](cep.md) - Validação de CEP e lookup de endereços
- [Inscrição Estadual](inscricao_estadual.md) - Validação completa de IE
- [Telefone](phone.md) - Validação de DDDs e telefones
- [CNPJ](cnpj.md) - Validação com informações regionais
