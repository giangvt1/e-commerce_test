// Main JavaScript for YCO Shop

document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize Bootstrap popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Cart quantity adjustment
    const quantityInputs = document.querySelectorAll('.quantity-input');
    if (quantityInputs) {
        quantityInputs.forEach(input => {
            const minusBtn = input.parentElement.querySelector('.quantity-minus');
            const plusBtn = input.parentElement.querySelector('.quantity-plus');
            
            if (minusBtn) {
                minusBtn.addEventListener('click', function() {
                    if (input.value > 1) {
                        input.value = parseInt(input.value) - 1;
                        triggerChange(input);
                    }
                });
            }
            
            if (plusBtn) {
                plusBtn.addEventListener('click', function() {
                    input.value = parseInt(input.value) + 1;
                    triggerChange(input);
                });
            }
        });
    }

    function triggerChange(input) {
        // Create and dispatch a change event
        const event = new Event('change', { bubbles: true });
        input.dispatchEvent(event);
    }

    // Product image gallery (if exists)
    const mainImage = document.querySelector('.product-main-image img');
    const thumbnails = document.querySelectorAll('.product-thumbnails img');
    
    if (mainImage && thumbnails.length > 0) {
        thumbnails.forEach(thumb => {
            thumb.addEventListener('click', function() {
                // Update main image
                mainImage.src = this.src;
                
                // Remove active class from all thumbnails and add to current
                thumbnails.forEach(t => t.classList.remove('active'));
                this.classList.add('active');
            });
        });
    }

    // Product quick view modal functionality
    const quickViewButtons = document.querySelectorAll('.quick-view-btn');
    if (quickViewButtons) {
        quickViewButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const productId = this.dataset.productId;
                
                // You would typically fetch product details via AJAX here
                fetch(`/api/products/${productId}/quick-view`)
                    .then(response => response.json())
                    .then(data => {
                        // Populate modal with product data
                        document.querySelector('#quickViewModal .modal-title').textContent = data.name;
                        document.querySelector('#quickViewModal .quick-view-price').textContent = data.price;
                        document.querySelector('#quickViewModal .quick-view-image').src = data.imageUrl;
                        document.querySelector('#quickViewModal .quick-view-description').textContent = data.description;
                        
                        // Show modal
                        const modal = new bootstrap.Modal(document.getElementById('quickViewModal'));
                        modal.show();
                    })
                    .catch(error => console.error('Error fetching product details:', error));
            });
        });
    }
});
