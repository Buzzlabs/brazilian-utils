(ns brazilian-utils.cep.internal)

(defn build-viacep-url
  "Builds a ViaCEP API URL for address lookup.
  
  Args:
    cep-clean - Cleaned CEP (digits only)
    
  Returns:
    URL string for ViaCEP API"
  [cep-clean]
  (let [base-url "https://viacep.com.br/ws"]
    (str base-url "/" cep-clean "/json")))

(defn build-viacep-address-search-url
  "Builds a ViaCEP API URL for searching CEP by address.
  
  Args:
    uf - State abbreviation
    localidade - City name
    logradouro - Street name
    
  Returns:
    URL string for ViaCEP API"
  [uf localidade logradouro]
  (let [base-url "https://viacep.com.br/ws"]
    (str base-url "/" uf "/" localidade "/" logradouro "/json")))
