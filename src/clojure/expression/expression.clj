(defn div
  ([denominator] (/ 1.0 (double denominator)))
  ([numerator & denominators] (reduce #(/ (double %1) (double %2)) numerator denominators)))

(defn constant [num]
  (constantly num))

(defn variable [name]
  #(get % name))

(defn operator [f]
  (fn [& args]
    (fn [vars]
      (apply f (mapv #(% vars) args)))))

(def negate (operator -))
(def add (operator +))
(def subtract (operator -))
(def multiply (operator *))
(def divide (operator div))

(defn square [arg] (* arg arg))
(defn mean_func [& args] (/ (apply + args) (count args)))
(defn varn_func [& args] (- (apply mean_func (mapv square args))
                            (square (apply mean_func args))))
(def mean (operator mean_func))
(def varn (operator varn_func))

(def OP_FUNC_MAP {'+ add
                   '- subtract
                   '* multiply
                   '/ divide
                   'negate negate
                   'mean mean
                   'varn varn})

(defn common_parser [operator_map constant variable]
  (fn [expr]
    (letfn [(parse_scope [scope_list]
              (apply (get operator_map (first scope_list)) (mapv parse_expr (rest scope_list))))
            (parse_expr [expr]
              (cond
                (number? expr) (constant expr)
                (list? expr) (parse_scope expr)
                :else (variable (str expr))))]
      (parse_expr (read-string expr)))))

(def parseFunction (common_parser OP_FUNC_MAP constant variable))



(load-file "proto.clj")

(def evaluate (method :evaluate))
(def toString (method :toString))
(def diff (method :diff))
(def toStringSuffix (method :toStringSuffix))
(def toStringInfix (method :toStringInfix))

(defn expression [eval_impl toStr_impl diff_impl toStrSuf_impl toStrInf_impl]
  {:evaluate eval_impl
   :toString toStr_impl
   :diff diff_impl
   :toStringSuffix toStrSuf_impl
   :toStringInfix toStrInf_impl})

(declare ZERO)
(def Constant
  (let [_val (field :val)]
    (constructor
      (fn [this val]
        (assoc this :val val))
      (expression
        (fn [this _] (_val this))
        (fn [this] (str (_val this)))
        (fn [this _] ZERO)
        (fn [this] (str (_val this)))
        (fn [this] (str (_val this)))))))
(def ZERO (Constant 0))
(def ONE (Constant 1))
(def TWO (Constant 2))

(def Variable
  (let [_name (field :name)]
    (constructor
      (fn [this name] (assoc this :name name))
      (expression
        (fn [this vars] (get vars (_name this)))
        (fn [this] (_name this))
        (fn [this var_name] (if (= var_name (_name this)) ONE ZERO))
        (fn [this] (_name this))
        (fn [this] (_name this))))))

(defn Operator [fun op diffRule]
  (let [_args (field :args)]
    (constructor
      (fn [this & args]
        (assoc this :args args))
      (expression
        (fn [this vars] (apply fun (mapv #(evaluate % vars) (_args this))))
        (fn [this] (str "(" op " " (clojure.string/join " " (mapv toString (_args this))) ")"))
        (fn [this var_name] (diffRule (_args this) (mapv #(diff % var_name) (_args this))))
        (fn [this] (str "(" (clojure.string/join " " (mapv toStringSuffix (_args this))) " " op ")"))
        (fn [this] (str "(" ((comp toStringInfix first) (_args this)) " " op
                        " " ((comp toStringInfix second) (_args this)) ")"))))))

(def Negate (Operator - 'negate (fn [_ diff_args] (apply Negate diff_args))))
(def Add (Operator + '+ (fn [_ diff_args] (apply Add diff_args))))
(def Subtract (Operator - '- (fn [_ diff_args] (apply Subtract diff_args))))

(defn zip [f s] (map vector f s))
(def Multiply
  (Operator
    *
    '*
    (fn [args diff_args]
      (second (reduce (fn [[a da] [b db]]
                        [(Multiply a b) (Add (Multiply a db) (Multiply b da))])
                      (zip args diff_args))))))
(def Divide
  (Operator
    div
    '/
    (fn [[arg & rest_args] [diff_arg & rest_diff_args]]
      (if (empty? rest_args)
        (Negate (Divide diff_arg (Multiply arg arg)))
        (let [denominator (apply Multiply rest_args)
              common_frac (Divide arg denominator)]
          (reduce Subtract (Divide diff_arg denominator)
                  (mapv (fn [[x dx]] (Multiply common_frac (Divide dx x)))
                        (zip rest_args rest_diff_args))))))))
(def Mean
  (Operator
    mean_func
    'mean
    (fn [_ diff_args]
      (Divide (apply Add diff_args) (Constant (count diff_args))))))
(def Varn
  (Operator
    varn_func
    'varn
    (fn [args diff_args]
      (Subtract
        (Divide
          (apply Add (mapv (fn [[x dx]] (Multiply x dx)) (zip args diff_args)))
          (Divide (Constant (count args)) TWO))
        (Divide
          (Multiply (apply Add args) (apply Add diff_args))
          (Divide (Constant (square (count args))) TWO))))))

(def OP_OBJ_MAP {'+ Add
                 '- Subtract
                 '* Multiply
                 '/ Divide
                 'negate Negate
                 'mean Mean
                 'varn Varn})

(def parseObject (common_parser OP_OBJ_MAP Constant Variable))
