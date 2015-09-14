(ns normalize.core
    (import [java.util Locale]))

(def as [#"[ÅåÆæ]" "a"])
(def oslash [#"[Øø]" "o"])
(def non-char [#"[^0-9a-z]" "-"])
(def multi-dash [#"-+" "-"])
(def appending-dash [#"^-" ""])
(def trailing-dash [#"-$" ""])

(def replacements [as oslash non-char multi-dash appending-dash trailing-dash])

(def locale (or (first (filter #(= "NO" (.getCountry %)) (Locale/getAvailableLocales))) Locale/ENGLISH))

(defn do-replace-re [a [regex replacement]]
  (clojure.string/replace a regex replacement))

(defn normalize-re [tag]
  (reduce do-replace-re (.toLowerCase tag locale) replacements))

(def normalize normalize-re)

(defn do-replace [v ^Character chr]
  (let [rank (int chr)]
    (cond
      (= 45 rank) (cond
                    (empty? v) v
                    (= \- (last v)) v
                    :else v)
      (= 230 rank) (conj v \a)
      (= 229 rank) (conj v \a)
      (= 248 rank) (conj v \o)
      :else (cond
              (and (> rank 47) (< rank 58)) (conj v chr)
              (and (> rank 96) (< rank 123)) (conj v chr)
              (= \- (last v)) v
              :else (conj v \-)))))

(defn normalize [^String tag]
  (let [tmp (reduce do-replace [] (.toLowerCase tag locale))
        ultimate (last tmp)]
    (apply str (if (= \- ultimate)
                 (butlast tmp)
                 tmp))))




;              (< rank 48) v
;              (> rank 122) v
;              (and (> rank 57) (< 96)) v

