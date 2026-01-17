# Capitalize - Capitalização de Strings em Português Brasileiro

Módulo para capitalização inteligente de strings com suporte a preposições portuguesas, conjunções, acrônimos e regras customizáveis. Respeita as convenções de capitaliação do português brasileiro.

## Visão Geral

O módulo `capitalize` fornece funções para capitalizar strings de forma adequada ao português brasileiro, com suporte especial para:
- Preposições e conjunções que devem ser minúsculas (exceto quando primeira palavra)
- Acrônimos que devem ser sempre maiúsculos
- Regras customizáveis por aplicação

**Principais capacidades:**
- ✅ Capitalização inteligente de strings
- ✅ Suporte a preposições e conjunções em português
- ✅ Tratamento especial de acrônimos brasileiros
- ✅ Regras de capitalização customizáveis
- ✅ Normalização de espaçamento múltiplo
- ✅ Preservação de estrutura de frases

## Estrutura

A capitalização segue regras de português brasileiro:

**Preposições padrão (minúsculas):**
a, com, da, das, de, do, dos, e, em, na, nas, no, nos, o, por, sem

**Acrônimos padrão (maiúsculos):**
cia, cnpj, cpf, ltda, me, rg

**Regras principais:**
1. Primeira palavra sempre é capitalizada
2. Preposições são minúsculas (exceto primeira palavra)
3. Acrônimos são sempre maiúsculos
4. Demais palavras recebem capitalização padrão (primeira letra maiúscula, resto minúscula)
5. Múltiplos espaços são normalizados para espaço único

## Funções Principais

### `capitalize`

Capitaliza uma string segundo as regras de português brasileiro.

**Assinatura:**
```clojure
(capitalize text)
(capitalize text options)
```

**Argumentos:**
- `text` - String para capitalizar
- `options` - Mapa opcional com:
  - `:lower-case-words` - Vetor de palavras que devem ser minúsculas (padrão: preposições)
  - `:upper-case-words` - Vetor de palavras que devem ser maiúsculas (padrão: acrônimos)

**Retorna:** String capitalizada

**Exemplos:**
```clojure
(capitalize "esponja de aço")
; => "Esponja de Aço"

(capitalize "josé ama maria")
; => "José Ama Maria"

(capitalize "josé ama maria" {:lower-case-words ["ama"]})
; => "José ama Maria"

(capitalize "doc da empresa ab" {:upper-case-words ["DOC" "AB"]})
; => "DOC da Empresa AB"

(capitalize "  múltiplos   espaços  ")
; => "Múltiplos Espaços"

(capitalize "")
; => ""

(capitalize "cpf da empresa ltda")
; => "CPF da Empresa LTDA"
```

## Recursos

- ✅ Capitalização inteligente respeitando português brasileiro
- ✅ Suporte a preposições padrão (a, de, do, da, em, com, etc.)
- ✅ Reconhecimento de acrônimos brasileiros (CPF, CNPJ, LTDA, etc.)
- ✅ Customização de palavras minúsculas e maiúsculas
- ✅ Normalização automática de espaçamento
- ✅ Tratamento de strings vazias
- ✅ Primeira palavra sempre capitalizada (regra de ouro)

## Tratamento de Erros

O módulo não lança exceções. Funciona com entradas diversas:

```clojure
;; Strings normais
(capitalize "josé da silva") ; => "José da Silva"

;; Strings com espaços extras
(capitalize "  josé   da   silva  ") ; => "José da Silva"

;; Strings vazias
(capitalize "") ; => ""

;; nil é convertido para string
(capitalize nil) ; => ""

;; Números e caracteres especiais
(capitalize "josé da silva 2024") ; => "José da Silva 2024"

;; Case-insensitive para palavras customizadas
(capitalize "JOSÉ DA SILVA" {:lower-case-words ["da"]})
; => "JOSÉ da SILVA"
```

## Casos de Uso

### 1. Formatação de Nomes de Pessoas

```clojure
(defn format-person-name [name]
  (capitalize name))

(format-person-name "joão silva santos")
; => "João Silva Santos"

(format-person-name "MARIA DA CONCEIÇÃO")
; => "Maria da Conceição"

(format-person-name "josé de oliveira neves")
; => "José de Oliveira Neves"
```

### 2. Títulos de Documentos

```clojure
(defn format-document-title [title]
  (capitalize title 
    {:upper-case-words ["RFC" "SQL" "API" "REST"]}))

(format-document-title "guia de desenvolvimento com api rest")
; => "Guia de Desenvolvimento com API REST"

(format-document-title "tutorial sobre sql e banco de dados")
; => "Tutorial sobre SQL e Banco de Dados"
```

### 3. Nomes de Empresas

```clojure
(defn format-company-name [name]
  (capitalize name 
    {:upper-case-words ["S/A" "LTDA" "EIRELI" "ME" "EPP"]}))

(format-company-name "empresa de tecnologia ltda")
; => "Empresa de Tecnologia LTDA"

(format-company-name "João da Silva me")
; => "João da Silva ME"

(format-company-name "softwares inovadores s/a")
; => "Softwares Inovadores S/A"
```

### 4. Endereços

```clojure
(defn format-address [address]
  (capitalize address 
    {:lower-case-words ["de" "do" "da" "dos" "das" "apt" "apto"]}))

(format-address "rua são joão de sousa nº 123 apt 456")
; => "Rua São João de Sousa Nº 123 Apt 456"

(format-address "avenida brasil com rua das flores")
; => "Avenida Brasil com Rua das Flores"
```

### 5. Formação de E-mail Display Name

```clojure
(defn format-email-display-name [email-name]
  (capitalize email-name))

(format-email-display-name "joão da silva santos")
; => "João da Silva Santos"

(format-email-display-name "maria do carmo oliveira")
; => "Maria do Carmo Oliveira"
```

### 6. Processamento em Lote

```clojure
(defn capitalize-batch [names]
  (map capitalize names))

(capitalize-batch 
  ["joão silva"
   "maria dos santos"
   "josé da conceição"
   "ana de oliveira"])
; => ["João Silva" "Maria dos Santos" "José da Conceição" "Ana de Oliveira"]
```

## Preposições Padrão

Palavras que são automaticamente mantidas em minúscula (exceto na primeira posição):

| Preposição | Tipo |
|-----------|------|
| a | Artigo/Preposição |
| com | Preposição |
| da | Preposição |
| das | Preposição |
| de | Preposição |
| do | Preposição |
| dos | Preposição |
| e | Conjunção |
| em | Preposição |
| na | Preposição |
| nas | Preposição |
| no | Preposição |
| nos | Preposição |
| o | Artigo/Preposição |
| por | Preposição |
| sem | Preposição |

## Acrônimos Padrão

Palavras que são automaticamente convertidas para maiúsculas:

| Acrônimo | Significado |
|----------|------------|
| cia | Companhia |
| cnpj | Cadastro Nacional de Pessoa Jurídica |
| cpf | Cadastro de Pessoa Física |
| ltda | Limitada |
| me | Microempresa |
| rg | Registro Geral |

## Ver Também

- [Email](email.md) - Validação de e-mails
- [Estados](states.md) - Informações de estados brasileiros
- [Cidades](cities.md) - Lookup de cidades por estado
