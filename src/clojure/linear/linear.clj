(defn isVector? [v]
  (and (vector? v) (every? number? v)))

(defn sameLength? [args]
  (and (every? vector? args) (every? (partial == (count (first args))) (mapv count args))))

(defn byElementOperator [f cond?]
  (fn [& args]
    {:pre [(every? cond? args) (sameLength? args)]
     :post [(cond? %)]}
    (apply mapv f args)))

(def v+ (byElementOperator + isVector?))
(def v- (byElementOperator - isVector?))
(def v* (byElementOperator * isVector?))
(def vd (byElementOperator / isVector?))

(defn v*s [v & args]
  {:pre [(isVector? v) (every? number? args)]
   :post [(isVector? %)]}
  (mapv (partial * (apply * args)) v))

(defn scalar [& args]
  {:pre [(every? isVector? args) (sameLength? args)]
   :post [number? %]}
  (apply + (reduce v* args)))

(defn vect [& args]
  {:pre [(every? isVector? args)  (== 3 (count (first args))) (sameLength? args)]
   :post [(isVector? %) (== 3 (count %))]}
  (reduce (fn [a b]
            (letfn [(det2in3 [i]
                      (letfn [(addQ3 [s] (rem (+ i s) 3))]
                        (- (* (nth a (addQ3 1)) (nth b (addQ3 2))) (* (nth a (addQ3 2)) (nth b (addQ3 1))))))]
            (vector (det2in3 0) (det2in3 1) (det2in3 2)))) args))

(defn isMatrix? [m]
  (and (vector? m) (every? isVector? m) (sameLength? m)))

(def m+ (byElementOperator v+ isMatrix?))
(def m- (byElementOperator v- isMatrix?))
(def m* (byElementOperator v* isMatrix?))
(def md (byElementOperator vd isMatrix?))

(defn m*s [m & args]
  {:pre [(isMatrix? m) (every? number? args)]
   :post [(isMatrix? m)]}
  (mapv #(v*s % (apply * args)) m))

(defn m*v [m v]
  {:pre [(isMatrix? m) (isVector? v) (sameLength? [(first m) v])]
   :post [(isVector? %) (sameLength? [m %])]}
  (mapv (partial apply +) (mapv #(v* v %) m)))

(defn transpose [m]
  {:pre [(isMatrix? m)]
   :post [(isMatrix? %)]}
  (apply mapv vector m))

(defn m*m [& args]
  {:pre [(every? isMatrix? args)]
   :post [(isMatrix? %)]}
  (reduce #(mapv (partial m*v (transpose %2)) %1) args))

(defn isSimplex? [s]
  (or (isVector? s)
      (let [size (count s)] (every? true? (map-indexed #(== (count %2) (- size %1)) s)))))

(defn simplexOperator [f cond?]
  (fn [& args]
    {:pre [(every? cond? args) (sameLength? args)]
     :post [(cond? %)]}
    (if (every? isVector? args)
      (apply f args)
      (apply mapv (simplexOperator f cond?) args))))

(def x+ (simplexOperator v+ isSimplex?))
(def x- (simplexOperator v- isSimplex?))
(def x* (simplexOperator v* isSimplex?))
(def xd (simplexOperator vd isSimplex?))
