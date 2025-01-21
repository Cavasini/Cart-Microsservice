    package com.cavasini.CartMicroservice.model;

    import jakarta.persistence.*;

    import java.io.Serializable;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.UUID;

    @Entity
    @Table(name = "products")
    public class Product implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO) // Gera automaticamente UUID
        @Column(name = "product_id", columnDefinition = "UUID")
        private UUID productId; // Alterado para UUID

        @Column(name = "name", nullable = false, length = 255)
        private String name;

        @Column(name = "description")
        private String description;

        @Column(name = "price", nullable = false, precision = 10, scale = 2)
        private BigDecimal price;

        @Column(name = "stock_quantity", nullable = false)
        private Integer stockQuantity;

        @Column(name = "category", nullable = false, length = 100)
        private String category;

        @Column(name = "seller_id", nullable = false)
        private Integer sellerId;

        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "status", length = 50)
        private String status = "available";

        @Column(name = "image_url", length = 500)
        private String imageUrl;

        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
        }

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getSellerId() {
            return sellerId;
        }

        public void setSellerId(Integer sellerId) {
            this.sellerId = sellerId;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "productId=" + productId +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", price=" + price +
                    ", stockQuantity=" + stockQuantity +
                    ", category='" + category + '\'' +
                    ", sellerId=" + sellerId +
                    ", createdAt=" + createdAt +
                    ", status='" + status + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    '}';
        }
    }