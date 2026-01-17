(ns brazilian-utils.inscricao-estadual-test
  (:require
   #?(:clj  [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer-macros [deftest is testing]])
   [brazilian-utils.inscricao-estadual :as ie]))

(def state-cases
  {:AC {:valid   ["0108368143106" "01.349.541/474-57"]
        :invalid ["0187634580933" "0187634580924" "0018763458000" "01018763458064"]}
   :AL {:valid   ["248659758" "247424170"]
        :invalid ["248659759" "258659750" "2486597584"]}
   :AM {:valid   ["48.063.523-4" "036029572" "000000019" "046893830"]
        :invalid ["036029573" "0036029572"]}
   :AP {:valid   ["036029572" "030123459" "030000080" "030000160" "030170011" "030170020" "030170071"]
        :invalid ["036029573" "0306029570" "003060292"]}
   :BA {:valid   ["12345663" "74219145" "038343081" "100000306" "778514741" "078771760" "039474751" "090529323" "04772253"]
        :invalid ["12345636" "74219154" "038343001" "778514731" "0012345636"]}
   :CE {:valid   ["853511942"]
        :invalid ["853511943" "0853511942"]}
   :DF {:valid   ["0754002000176" "0754002000508"]
        :invalid ["0108368143017" "07008368143094" "0754002000175"]}
   :ES {:valid   ["639191444"]
        :invalid ["639191445" "0639191444"]}
   :GO {:valid   ["109161793" "101031131" "101030940"]
        :invalid ["109161794" "121031131" "0101030940"]}
   :MA {:valid   ["120000008" "120000040" "120000130"]
        :invalid ["120000007" "109161793" "0120000008"]}
   :MG {:valid   ["4333908330177" "4333908330410" "7489439278602" "4333908332560"]
        :invalid ["4333908330167" "04333908330177" "4333908330176"]}
   :MS {:valid   ["280000006" "280000090" "280000030"]
        :invalid ["280000031" "0280000006" "853511942"]}
   :MT {:valid   ["60474120469"]
        :invalid ["12345678901" "1234567890112"]}
   :PA {:valid   ["150000006" "150000260" "150000030"]
        :invalid ["120000008" "0150000006" "150000007"]}
   :PB {:valid   ["853511942" "853512230" "853511950"]
        :invalid ["0853511942" "853511943"]}
   :PE {:valid   ["288625706"]
        :invalid ["0925870110" "925870101"]}
   :PI {:valid   ["052364534"]
        :invalid []}
   :PR {:valid   ["4447953604"]
        :invalid ["04447953604" "4447953640"]}
   :RJ {:valid   ["62545372" "62545470" "62545380"]
        :invalid ["20441620" "020441623"]}
   :RN {:valid   ["2007693232" "2003569880" "203569881"]
        :invalid ["2007693231" "0203569881" "20356988104"]}
   :RO {:valid   ["01078042249629" "01078042249670" "01078042249751"]
        :invalid ["01078042249756" "001078042249627"]}
   :RR {:valid   ["240061536"]
        :invalid ["240061537" "2400615366" "024006150"]}
   :RS {:valid   ["0305169149" "1202762660" "1202762120"]
        :invalid ["2007693232" "02007693230"]}
   :SC {:valid   ["330430572"]
        :invalid []}
   :SE {:valid   ["017682606"]
        :invalid []}
   :SP {:valid   ["110042490114"]
        :invalid ["1110042494114" "110042490113" "110042498113"]}
   :TO {:valid   ["01027737427" "294467696" "294150870"]
        :invalid ["01047737427" "099999916599" "99999916598" "294467690"]}})

(defn- assert-cases [expected uf cases]
  (doseq [ie cases]
    (is (= expected (ie/is-valid? uf ie))
        (str (name uf) " IE " ie " should be " expected))))

;; ============================================================================
;; State validations based on brazilian-utils/javascript test vectors
;; ============================================================================

(deftest per-state-validation-test
  (doseq [[uf {:keys [valid invalid]}] state-cases]
    (testing (str "accepts valid IE for " (name uf))
      (assert-cases true uf valid))
    (when (seq invalid)
      (testing (str "rejects invalid IE for " (name uf))
        (assert-cases false uf invalid)))))

;; ============================================================================
;; Tests: remove-symbols
;; ============================================================================

(deftest remove-symbols-test
  (testing "removes formatting characters"
    (is (= "110042490114" (ie/remove-symbols "11.004.249.0114")))
    (is (= "110042490114" (ie/remove-symbols "11 004 249 0114")))
    (is (= "110042490114" (ie/remove-symbols "11-004-249-0114"))))

  (testing "handles plain numbers"
    (is (= "110042490114" (ie/remove-symbols "110042490114"))))

  (testing "handles nil and empty string"
    (is (= "" (ie/remove-symbols nil)))
    (is (= "" (ie/remove-symbols "")))))

;; ============================================================================
;; Tests: Edge cases
;; ============================================================================

(deftest edge-cases-test
  (testing "handles nil IE"
    (is (false? (ie/is-valid? :SP nil))))

  (testing "handles empty string"
    (is (false? (ie/is-valid? :SP ""))))

  (testing "handles IE with all zeros"
    (is (false? (ie/is-valid? :SP "000000000000"))))

  (testing "handles IE with letters"
    (is (false? (ie/is-valid? :SP "11004249011A")))))